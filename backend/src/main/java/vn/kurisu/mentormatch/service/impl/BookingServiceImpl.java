package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.BookingRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.BookingResponse;
import vn.kurisu.mentormatch.entity.Booking;
import vn.kurisu.mentormatch.entity.BookingStatus;
import vn.kurisu.mentormatch.entity.SlotStatus;
import vn.kurisu.mentormatch.entity.TimeSlot;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.BookingRepository;
import vn.kurisu.mentormatch.repository.TimeSlotRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.BookingService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

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
        User currentMentee = getCurrentUser();

        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (timeSlot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("Time slot is no longer available");
        }

        if (timeSlot.getMentor().getId().equals(currentMentee.getId())) {
            throw new RuntimeException("You cannot book your own time slot");
        }

        // Update slot status
        timeSlot.setStatus(SlotStatus.BOOKED);
        timeSlotRepository.save(timeSlot);

        // Create booking
        Booking booking = Booking.builder()
                .mentee(currentMentee)
                .timeSlot(timeSlot)
                .menteeNotes(request.getMenteeNotes())
                .status(BookingStatus.PENDING)
                .build();

        booking = bookingRepository.save(booking);

        return ApiResponse.<BookingResponse>builder()
                .result(mapToBookingResponse(booking))
                .build();
    }

    @Override
    public ApiResponse<List<BookingResponse>> getMyTraineeBookings() {
        User currentMentee = getCurrentUser();

        List<BookingResponse> responses = bookingRepository.findByMenteeIdOrderByCreatedAtDesc(currentMentee.getId())
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<BookingResponse>>builder()
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
                .build();
    }
}
