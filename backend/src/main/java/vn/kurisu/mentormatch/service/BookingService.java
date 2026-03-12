package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.request.BookingRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {
    ApiResponse<BookingResponse> createBooking(BookingRequest request);
    ApiResponse<List<BookingResponse>> getMyTraineeBookings();
    ApiResponse<BookingResponse> cancelBooking(Integer id);
    ApiResponse<BookingResponse> completeBooking(Integer id);
}
