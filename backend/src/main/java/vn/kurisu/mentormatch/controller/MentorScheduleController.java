package vn.kurisu.mentormatch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.BookingActionRequest;
import vn.kurisu.mentormatch.dto.request.MeetingLinkRequest;
import vn.kurisu.mentormatch.dto.request.TimeSlotRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.BookingResponse;
import vn.kurisu.mentormatch.dto.response.TimeSlotResponse;
import vn.kurisu.mentormatch.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/time-slots")
    public ApiResponse<TimeSlotResponse> createTimeSlot(@RequestBody @Valid TimeSlotRequest request) {
        return scheduleService.createTimeSlot(request);
    }

    @GetMapping("/time-slots")
    public ApiResponse<List<TimeSlotResponse>> getMyTimeSlots() {
        return scheduleService.getMyTimeSlots();
    }

    @DeleteMapping("/time-slots/{id}")
    public ApiResponse<TimeSlotResponse> deleteTimeSlot(@PathVariable Integer id){
        return scheduleService.deleteTimeSlot(id);
    }
    @PutMapping("/time-slots/{id}")
    public ApiResponse<TimeSlotResponse> updateTimeSlot(@PathVariable Integer id, @RequestBody @Valid TimeSlotRequest request){
        return scheduleService.updateTimeSlot(id, request);
    }

    @GetMapping("/bookings")
    public ApiResponse<org.springframework.data.domain.Page<BookingResponse>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return scheduleService.getMyBookings(pageable, status);
    }

    @PutMapping("/bookings/{bookingId}/status")
    public ApiResponse<BookingResponse> processBooking(
            @PathVariable Integer bookingId,
            @RequestBody @Valid BookingActionRequest request) {
        return scheduleService.processBooking(bookingId, request);
    }

    @PutMapping("/bookings/{bookingId}/meeting-link")
    public ApiResponse<BookingResponse> updateMeetingLink(
            @PathVariable Integer bookingId,
            @RequestBody @Valid MeetingLinkRequest request) {
        return scheduleService.updateMeetingLink(bookingId, request);
    }
}
