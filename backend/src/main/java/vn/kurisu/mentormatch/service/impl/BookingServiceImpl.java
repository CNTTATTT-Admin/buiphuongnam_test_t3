package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.BookingRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.BookingResponse;
import vn.kurisu.mentormatch.dto.response.VNPayPaymentResponse;
import vn.kurisu.mentormatch.entity.*;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.BookingRepository;
import vn.kurisu.mentormatch.repository.MentorProfileRepository;
import vn.kurisu.mentormatch.repository.TimeSlotRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.BookingService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final VNPayService vnPayService;

    @Value("${frontend.paymentResultUrl:http://localhost:5173/payment-result}")
    private String paymentResultUrl;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    @Override
    @Transactional
    public ApiResponse<BookingResponse> createBooking(BookingRequest request) {
        Booking booking = createAndPersistBooking(request);

        return ApiResponse.<BookingResponse>builder()
                .result(mapToBookingResponse(booking))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<VNPayPaymentResponse> createBookingWithVNPay(BookingRequest request) {
        Booking booking = createAndPersistBooking(request);

        BigDecimal price = booking.getTimeSlot().getPrice();
        int amount = price != null ? price.intValue() : 0;

        String orderInfo = "Thanh toan buoi hoc #" + booking.getId()
                + " voi mentor " + booking.getTimeSlot().getMentor().getFullName();

        String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, booking.getId().toString());

        VNPayPaymentResponse vnpResponse = VNPayPaymentResponse.builder()
                .booking(mapToBookingResponse(booking))
                .paymentUrl(paymentUrl)
                .build();

        return ApiResponse.<VNPayPaymentResponse>builder()
                .result(vnpResponse)
                .build();
    }

    @Override
    @Transactional
    public String handleVNPayReturn(Map<String, String> vnpParams) {
        boolean validSignature = vnPayService.validateCallback(vnpParams);
        String defaultRedirect = paymentResultUrl + "?success=false&message=" +
                urlEncode("Chữ ký không hợp lệ");

        if (!validSignature) {
            return defaultRedirect;
        }

        String txnRef = vnpParams.get("vnp_TxnRef");
        if (txnRef == null) {
            return paymentResultUrl + "?success=false&message=" +
                    urlEncode("Thiếu mã giao dịch");
        }

        Integer bookingId;
        try {
            bookingId = Integer.valueOf(txnRef);
        } catch (NumberFormatException e) {
            return paymentResultUrl + "?success=false&message=" +
                    urlEncode("Mã giao dịch không hợp lệ");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElse(null);
        if (booking == null) {
            return paymentResultUrl + "?success=false&message=" +
                    urlEncode("Không tìm thấy booking");
        }

        boolean success = vnPayService.isPaymentSuccess(vnpParams);
        String message;

        if (success) {
            if (booking.getStatus() == BookingStatus.PENDING) {
                booking.setStatus(BookingStatus.PAID);

                TimeSlot timeSlot = booking.getTimeSlot();
                if (timeSlot != null && timeSlot.getStatus() == SlotStatus.AVAILABLE) {
                    timeSlot.setStatus(SlotStatus.BOOKED);
                    timeSlotRepository.save(timeSlot);
                }

                bookingRepository.save(booking);
            }
            message = "Thanh toán thành công";
        } else {
            if (booking.getStatus() == BookingStatus.PENDING) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);

                TimeSlot timeSlot = booking.getTimeSlot();
                if (timeSlot != null && timeSlot.getStatus() == SlotStatus.BOOKED) {
                    timeSlot.setStatus(SlotStatus.AVAILABLE);
                    timeSlotRepository.save(timeSlot);
                }
            }
            message = "Thanh toán thất bại hoặc bị hủy";
        }

        return paymentResultUrl
                + "?success=" + success
                + "&bookingId=" + booking.getId()
                + "&message=" + urlEncode(message);
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private Booking createAndPersistBooking(BookingRequest request) {
        User currentMentee = getCurrentUser();

        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (timeSlot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Time slot is no longer available");
        }

        if (timeSlot.getMentor().getId().equals(currentMentee.getId())) {
            throw new RuntimeException("You cannot book your own time slot");
        }

        // Tạo booking ở trạng thái PENDING, giữ slot ở trạng thái AVAILABLE
        Booking booking = Booking.builder()
                .mentee(currentMentee)
                .timeSlot(timeSlot)
                .menteeNotes(request.getMenteeNotes())
                .status(BookingStatus.PENDING)
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public ApiResponse<BookingResponse> cancelBooking(Integer id) {
        User currentUser = getCurrentUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getMentee().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to cancel this booking");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return ApiResponse.<BookingResponse>builder()
                .result(mapToBookingResponse(booking))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<BookingResponse> completeBooking(Integer id) {
        User currentMentee = getCurrentUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getMentee().getId().equals(currentMentee.getId())) {
            throw new RuntimeException("You are not authorized to complete this booking");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed bookings can be completed");
        }

        // Mark as completed
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // Transfer slot price to mentor's wallet
        TimeSlot timeSlot = booking.getTimeSlot();
        User mentor = timeSlot.getMentor();
        MentorProfile mentorProfile = mentor.getMentorProfile();

        if (mentorProfile != null) {
            mentorProfile.setWalletBalance(
                    mentorProfile.getWalletBalance().add(timeSlot.getPrice())
            );
            mentorProfileRepository.save(mentorProfile);
        }

        return ApiResponse.<BookingResponse>builder()
                .result(mapToBookingResponse(booking))
                .message("Booking completed. " + timeSlot.getPrice() + " VND transferred to mentor's wallet.")
                .build();
    }

    @Override
    public ApiResponse<org.springframework.data.domain.Page<BookingResponse>> getMyTraineeBookings(org.springframework.data.domain.Pageable pageable, String status) {
        User currentMentee = getCurrentUser();

        BookingStatus bookingStatus = null;
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
            try {
                bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        org.springframework.data.domain.Page<BookingResponse> responses = bookingRepository.findByMenteeIdOrderByCreatedAtDesc(currentMentee.getId(), bookingStatus, pageable)
                .map(this::mapToBookingResponse);

        return ApiResponse.<org.springframework.data.domain.Page<BookingResponse>>builder()
                .result(responses)
                .build();
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .menteeId(booking.getMentee().getId())
                .menteeName(booking.getMentee().getFullName())
                .menteeAvatar(booking.getMentee().getAvatarUrl())
                .mentorId(booking.getTimeSlot().getMentor().getId())
                .mentorName(booking.getTimeSlot().getMentor().getFullName())
                .mentorAvatar(booking.getTimeSlot().getMentor().getAvatarUrl())
                .timeSlotId(booking.getTimeSlot().getId())
                .startTime(booking.getTimeSlot().getStartTime())
                .endTime(booking.getTimeSlot().getEndTime())
                .menteeNotes(booking.getMenteeNotes())
                .meetingLink(booking.getMeetingLink())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .price(booking.getTimeSlot().getPrice())
                .build();
    }
}

