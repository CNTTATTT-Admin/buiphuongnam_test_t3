package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.dto.request.BookingActionRequest;
import vn.kurisu.mentormatch.dto.request.MeetingLinkRequest;
import vn.kurisu.mentormatch.dto.request.TimeSlotRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.BookingResponse;
import vn.kurisu.mentormatch.dto.response.TimeSlotResponse;
import vn.kurisu.mentormatch.entity.*;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.BookingRepository;
import vn.kurisu.mentormatch.repository.TimeSlotRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.ScheduleService;

import java.util.List;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;
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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void verifyMentorRole(User user) {
        boolean isMentor = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_MENTOR") || role.getName().equals("ROLE_ADMIN"));
        if (!isMentor) {
            throw new RuntimeException("Only mentors can perform this action");
        }
    }
    @Override
    public ApiResponse<TimeSlotResponse> deleteTimeSlot(Integer id) {
        User mentor = getCurrentUser();
        verifyMentorRole(mentor);
        TimeSlot timeSlot = timeSlotRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.TIME_SLOT_NOT_FOUND));
        if (!timeSlot.getMentor().getId().equals(mentor.getId())){
            throw new RuntimeException("You do not have permission to delete this time slot");
        }
        timeSlotRepository.delete(timeSlot);
        return ApiResponse.<TimeSlotResponse>builder()
                .message("Time slot deleted successfully")
                .result(mapToTimeSlotResponse(timeSlot))
                .build();
    }
    @Override
    public ApiResponse<TimeSlotResponse> updateTimeSlot(Integer id, TimeSlotRequest request) {
        User mentor = getCurrentUser();
        verifyMentorRole(mentor);
        TimeSlot timeSlot = timeSlotRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.TIME_SLOT_NOT_FOUND));
        if (!timeSlot.getMentor().getId().equals(mentor.getId())){
            throw new RuntimeException("You do not have permission to update this time slot");
        }
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setPrice(request.getPrice());
        timeSlot = timeSlotRepository.save(timeSlot);
        return ApiResponse.<TimeSlotResponse>builder()
                .message("Time slot updated successfully")
                .result(mapToTimeSlotResponse(timeSlot))
                .build();
    }

    @Override
    public ApiResponse<TimeSlotResponse> createTimeSlot(TimeSlotRequest request) {
        User mentor = getCurrentUser();
        verifyMentorRole(mentor);

        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        TimeSlot timeSlot = TimeSlot.builder()
                .mentor(mentor)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .status(SlotStatus.AVAILABLE)
                .build();

        timeSlot = timeSlotRepository.save(timeSlot);

        return ApiResponse.<TimeSlotResponse>builder()
                .message("Time slot created successfully")
                .result(mapToTimeSlotResponse(timeSlot))
                .build();
    }

    @Override
    public ApiResponse<List<TimeSlotResponse>> getMyTimeSlots() {
        User mentor = getCurrentUser();
        List<TimeSlotResponse> slots = timeSlotRepository.findByMentorIdOrderByStartTimeAsc(mentor.getId())
                .stream()
                .map(this::mapToTimeSlotResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<TimeSlotResponse>>builder()
                .result(slots)
                .build();
    }

    @Override
    public ApiResponse<List<BookingResponse>> getMyBookings() {
        User mentor = getCurrentUser();
        List<BookingResponse> bookings = bookingRepository.findBookingsByMentorId(mentor.getId())
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookings)
                .build();
    }

    @Override
    public ApiResponse<BookingResponse> processBooking(Integer bookingId, BookingActionRequest request) {
        User mentor = getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getTimeSlot().getMentor().getId().equals(mentor.getId())) {
            throw new RuntimeException("Not authorized to process this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Can only process PENDING bookings");
        }

        if ("CONFIRM".equalsIgnoreCase(request.getAction())) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.getTimeSlot().setStatus(SlotStatus.BOOKED);
            timeSlotRepository.save(booking.getTimeSlot());
        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            booking.setStatus(BookingStatus.REJECTED);
            booking.getTimeSlot().setStatus(SlotStatus.AVAILABLE);
            timeSlotRepository.save(booking.getTimeSlot());
        } else {
            throw new RuntimeException("Invalid action. Use CONFIRM or REJECT.");
        }

        booking = bookingRepository.save(booking);

        return ApiResponse.<BookingResponse>builder()
                .message("Booking " + request.getAction().toLowerCase() + "ed successfully")
                .result(mapToBookingResponse(booking))
                .build();
    }

    @Override
    public ApiResponse<BookingResponse> updateMeetingLink(Integer bookingId, MeetingLinkRequest request) {
        User mentor = getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getTimeSlot().getMentor().getId().equals(mentor.getId())) {
            throw new RuntimeException("Not authorized to update this booking");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Can only add meeting link to CONFIRMED bookings");
        }

        booking.setMeetingLink(request.getMeetingLink());
        booking = bookingRepository.save(booking);

        return ApiResponse.<BookingResponse>builder()
                .message("Meeting link updated successfully")
                .result(mapToBookingResponse(booking))
                .build();
    }

    private TimeSlotResponse mapToTimeSlotResponse(TimeSlot slot) {
        return TimeSlotResponse.builder()
                .id(slot.getId())
                .mentorId(slot.getMentor().getId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .price(slot.getPrice())
                .status(slot.getStatus())
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
