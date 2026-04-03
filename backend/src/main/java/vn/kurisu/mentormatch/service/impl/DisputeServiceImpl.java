package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.CreateDisputeRequest;
import vn.kurisu.mentormatch.dto.request.CounterDisputeRequest;
import vn.kurisu.mentormatch.dto.request.ResolveDisputeRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.DisputeResponse;
import vn.kurisu.mentormatch.entity.*;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.*;
import vn.kurisu.mentormatch.service.DisputeService;
import vn.kurisu.mentormatch.service.NotificationService;

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
    private final NotificationService notificationService;

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

        // Send notification to the other party
        User otherParty = currentUser.getId().equals(booking.getMentee().getId()) 
                          ? booking.getTimeSlot().getMentor() 
                          : booking.getMentee();
        
        notificationService.sendNotification(
            otherParty,
            "Có khiếu nại mới",
            "Ca học #" + booking.getId() + " vừa bị khiếu nại. Vui lòng vào Lịch học để xem chi tiết và gửi Kháng cáo (nếu có).",
            "DISPUTE_CREATED",
            booking.getId()
        );

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
    @Transactional
    public ApiResponse<DisputeResponse> counterDispute(Integer disputeId, CounterDisputeRequest request) {
        User currentUser = getCurrentUser();
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found"));

        if (!"PENDING".equals(dispute.getStatus())) {
            throw new RuntimeException("Đơn khiếu nại đã được xử lý hoặc không hợp lệ.");
        }

        if (dispute.getCounterReason() != null) {
            throw new RuntimeException("Bạn đã gửi kháng cáo rồi.");
        }

        Booking booking = dispute.getBooking();
        User mentee = booking.getMentee();
        User mentor = booking.getTimeSlot().getMentor();

        boolean isAuthorized = currentUser.getId().equals(mentee.getId()) || currentUser.getId().equals(mentor.getId());
        if (!isAuthorized || currentUser.getId().equals(dispute.getCreator().getId())) {
            throw new RuntimeException("Bạn không có quyền kháng cáo đơn này.");
        }

        dispute.setCounterReason(request.getCounterReason());
        dispute.setCounterCreator(currentUser);
        dispute.setRespondedAt(LocalDateTime.now());
        dispute.setStatus("APPEALED"); // Vẫn tính là Chờ xử lý nhưng đã có kháng đơn

        Dispute savedDispute = disputeRepository.save(dispute);

        // Inform admin and creator
        notificationService.sendNotification(
            dispute.getCreator(),
            "Đối phương đã gửi kháng cáo",
            "Đối phương trong ca học #" + booking.getId() + " vừa gửi kháng cáo đối với đơn khiếu nại của bạn.",
            "DISPUTE_APPEALED",
            booking.getId()
        );

        return ApiResponse.<DisputeResponse>builder()
                .result(mapToResponse(savedDispute))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DisputeResponse> getDisputeByBooking(Integer bookingId) {
        User currentUser = getCurrentUser();
        Dispute dispute = disputeRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khiếu nại cho ca học này"));

        Booking booking = dispute.getBooking();
        User mentee = booking.getMentee();
        User mentor = booking.getTimeSlot().getMentor();

        // Check if user is part of the booking or admin
        boolean isAuthorized = currentUser.getId().equals(mentee.getId()) || 
                               currentUser.getId().equals(mentor.getId()) ||
                               currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        
        if (!isAuthorized) {
            throw new RuntimeException("Bạn không có quyền xem khiếu nại này.");
        }

        return ApiResponse.<DisputeResponse>builder()
                .result(mapToResponse(dispute))
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

        if (!"PENDING".equals(dispute.getStatus()) && !"APPEALED".equals(dispute.getStatus())) {
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
            // B4: Gửi Notification vào DB và RabbitMQ
            // Gửi Mentee
            notificationService.sendNotification(
                    mentee,
                    "Hoàn tiền thành công",
                    "Yêu cầu khiếu nại thành công, tiền đang được hoàn về thẻ của ca học #" + booking.getId() + ".",
                    "REFUND",
                    booking.getId()
            );
                    
            // Gửi Mentor
            notificationService.sendNotification(
                    mentor,
                    "Thu hồi doanh thu",
                    "Hệ thống đã thu hồi " + amountToDeduct + " VND của ca học #" + booking.getId() + " do quyết định khiếu nại. Số dư ví đã được cập nhật.",
                    "REFUND",
                    booking.getId()
            );

        } else {
            dispute.setStatus("RESOLVED_NO_REFUND");
            // Trả lại trạng thái cũ cho Booking, giả sử là COMPLETED
            booking.setStatus(BookingStatus.COMPLETED); 
            
            // Gửi thông báo từ chối tới Mentee
            notificationService.sendNotification(
                    mentee,
                    "Kết quả khiếu nại",
                    "Yêu cầu khiếu nại ca học #" + booking.getId() + " không được chấp nhận. Lý do: " + request.getAdminNote(),
                    "DISPUTE_REJECTED",
                    booking.getId()
            );
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
                .counterReason(dispute.getCounterReason())
                .counterCreatorId(dispute.getCounterCreator() != null ? dispute.getCounterCreator().getId() : null)
                .counterCreatorName(dispute.getCounterCreator() != null ? dispute.getCounterCreator().getFullName() : null)
                .respondedAt(dispute.getRespondedAt())
                .createdAt(dispute.getCreatedAt())
                .resolvedAt(dispute.getResolvedAt())
                .build();
    }
}
