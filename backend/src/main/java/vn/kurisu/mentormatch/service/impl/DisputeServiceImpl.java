package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.CreateDisputeRequest;
import vn.kurisu.mentormatch.dto.request.NotificationEventDto;
import vn.kurisu.mentormatch.dto.request.ResolveDisputeRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.DisputeResponse;
import vn.kurisu.mentormatch.entity.*;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.*;
import vn.kurisu.mentormatch.service.DisputeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;
    private final VNPayService vnPayService;
    private final RabbitMQProducer rabbitMQProducer;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    @Override
    @Transactional
    public ApiResponse<DisputeResponse> createDispute(CreateDisputeRequest request) {
        User currentUser = getCurrentUser();
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Only COMPLETED, PAID, or CONFIRMED bookings can be disputed
        if (booking.getStatus() != BookingStatus.COMPLETED
                && booking.getStatus() != BookingStatus.PAID
                && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Chỉ có thể khiếu nại các ca học đã thanh toán, đã xác nhận hoặc đã hoàn thành. Trạng thái hiện tại: " + booking.getStatus());
        }

        Dispute dispute = Dispute.builder()
                .booking(booking)
                .creator(currentUser)
                .reason(request.getReason())
                .status("PENDING")
                .build();
                
        booking.setStatus(BookingStatus.DISPUTED);
        bookingRepository.save(booking);

        Dispute savedDispute = disputeRepository.save(dispute);
        return ApiResponse.<DisputeResponse>builder()
                .result(mapToResponse(savedDispute))
                .build();
    }

    @Override
    public ApiResponse<Page<DisputeResponse>> getMyDisputes(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<DisputeResponse> responses = disputeRepository
                .findByCreatorIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(this::mapToResponse);

        return ApiResponse.<Page<DisputeResponse>>builder()
                .result(responses)
                .build();
    }

    @Override
    public ApiResponse<Page<DisputeResponse>> getMentorDisputes(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<DisputeResponse> responses = disputeRepository
                .findByBooking_TimeSlot_MentorIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(this::mapToResponse);

        return ApiResponse.<Page<DisputeResponse>>builder()
                .result(responses)
                .build();
    }

    @Override
    public ApiResponse<Page<DisputeResponse>> getAllDisputes(Pageable pageable) {
        return ApiResponse.<Page<DisputeResponse>>builder()
                .result(disputeRepository.findAll(pageable).map(this::mapToResponse))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<DisputeResponse> resolveDispute(Integer disputeId, ResolveDisputeRequest request) {
        User adminUser = getCurrentUser(); // Admin
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found"));

        if (!"PENDING".equals(dispute.getStatus())) {
            throw new RuntimeException("Khiếu nại này đã được xử lý trước đó.");
        }

        dispute.setAdminNote(request.getAdminNote());
        dispute.setResolvedAt(LocalDateTime.now());
        Booking booking = dispute.getBooking();
        User mentee = booking.getMentee();
        User mentor = booking.getTimeSlot().getMentor();

        if (request.getAcceptRefund()) {
            dispute.setStatus("RESOLVED_REFUND");
            booking.setStatus(BookingStatus.REFUNDED);

            // B1: Clawback (Trừ tiền ví Mentor)
            MentorProfile mentorProfile = mentor.getMentorProfile();
            BigDecimal amountToDeduct = booking.getTimeSlot().getPrice();
            if (mentorProfile != null && mentorProfile.getWalletBalance().compareTo(amountToDeduct) >= 0) {
                mentorProfile.setWalletBalance(mentorProfile.getWalletBalance().subtract(amountToDeduct));
                mentorProfileRepository.save(mentorProfile);
            } else {
                log.warn("Mentor wallet balance insufficient or profile not found for deduction. Proceeding anyway.");
                if (mentorProfile != null) {
                    mentorProfile.setWalletBalance(mentorProfile.getWalletBalance().subtract(amountToDeduct));
                    mentorProfileRepository.save(mentorProfile);
                }
            }

            // Ghi nhận giao dịch trừ tiền
            Payment deductionPayment = Payment.builder()
                    .booking(booking)
                    .amount(amountToDeduct.negate()) // Số lưu âm
                    .status(PaymentStatus.REFUND_DEDUCTION)
                    .paidAt(LocalDateTime.now())
                    .build();
            paymentRepository.save(deductionPayment);

            // B2 & B3: Gọi VNPay Refund API
            Payment originalPayment = paymentRepository.findByBookingIdAndStatus(booking.getId(), PaymentStatus.SUCCESS)
                    .orElse(null);
            if (originalPayment != null) {
                vnPayService.refundTransaction(originalPayment, adminUser.getUserName(), mentee.getEmail());
            } else {
                log.error("Original successful payment not found for booking {}", booking.getId());
            }

            // B4: Bắn sự kiện RabbitMQ
            // Gửi Mentee
            rabbitMQProducer.sendNotificationEvent(NotificationEventDto.builder()
                    .userId(mentee.getId().longValue())
                    .title("Hoàn tiền thành công")
                    .message("Yêu cầu khiếu nại thành công, tiền đang được hoàn về thẻ của ca học #" + booking.getId() + ".")
                    .type("REFUND")
                    .referenceId(booking.getId().longValue())
                    .build());
                    
            // Gửi Mentor
            rabbitMQProducer.sendNotificationEvent(NotificationEventDto.builder()
                    .userId(mentor.getId().longValue())
                    .title("Thu hồi doanh thu")
                    .message("Hệ thống đã thu hồi " + amountToDeduct + " VND của ca học #" + booking.getId() + " do quyết định khiếu nại. Số dư ví đã được cập nhật.")
                    .type("REFUND")
                    .referenceId(booking.getId().longValue())
                    .build());

        } else {
            dispute.setStatus("RESOLVED_NO_REFUND");
            // Trả lại trạng thái cũ cho Booking, giả sử là COMPLETED
            booking.setStatus(BookingStatus.COMPLETED); 
            
            // Gửi thông báo từ chối tới Mentee
            rabbitMQProducer.sendNotificationEvent(NotificationEventDto.builder()
                    .userId(mentee.getId().longValue())
                    .title("Kết quả khiếu nại")
                    .message("Yêu cầu khiếu nại ca học #" + booking.getId() + " không được chấp nhận. Lý do: " + request.getAdminNote())
                    .type("DISPUTE_REJECTED")
                    .referenceId(booking.getId().longValue())
                    .build());
        }

        bookingRepository.save(booking);
        Dispute savedDispute = disputeRepository.save(dispute);
        
        return ApiResponse.<DisputeResponse>builder()
                .result(mapToResponse(savedDispute))
                .build();
    }

    private DisputeResponse mapToResponse(Dispute dispute) {
        return DisputeResponse.builder()
                .id(dispute.getId())
                .bookingId(dispute.getBooking().getId())
                .creatorId(dispute.getCreator().getId())
                .creatorName(dispute.getCreator().getFullName())
                .reason(dispute.getReason())
                .status(dispute.getStatus())
                .adminNote(dispute.getAdminNote())
                .createdAt(dispute.getCreatedAt())
                .resolvedAt(dispute.getResolvedAt())
                .build();
    }
}
