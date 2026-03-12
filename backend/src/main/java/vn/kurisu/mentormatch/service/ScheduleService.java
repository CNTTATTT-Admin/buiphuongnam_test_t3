package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.request.BookingActionRequest;
import vn.kurisu.mentormatch.dto.request.MeetingLinkRequest;
import vn.kurisu.mentormatch.dto.request.TimeSlotRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.BookingResponse;
import vn.kurisu.mentormatch.dto.response.TimeSlotResponse;

import java.util.List;

public interface ScheduleService {
    // For Mentors
    ApiResponse<TimeSlotResponse> deleteTimeSlot(Integer id);
    ApiResponse<TimeSlotResponse> updateTimeSlot(Integer id, TimeSlotRequest request);
    ApiResponse<TimeSlotResponse> createTimeSlot(TimeSlotRequest request);
    ApiResponse<List<TimeSlotResponse>> getMyTimeSlots();
    ApiResponse<List<BookingResponse>> getMyBookings();
    ApiResponse<BookingResponse> processBooking(Integer bookingId, BookingActionRequest request);
    ApiResponse<BookingResponse> updateMeetingLink(Integer bookingId, MeetingLinkRequest request);
}
