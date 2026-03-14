package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.request.BookingRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.BookingResponse;
import vn.kurisu.mentormatch.dto.response.VNPayPaymentResponse;

import java.util.List;
import java.util.Map;

public interface BookingService {
    ApiResponse<BookingResponse> createBooking(BookingRequest request);

    ApiResponse<VNPayPaymentResponse> createBookingWithVNPay(BookingRequest request);

    String handleVNPayReturn(Map<String, String> vnpParams);

    ApiResponse<List<BookingResponse>> getMyTraineeBookings();

    ApiResponse<BookingResponse> cancelBooking(Integer id);

    ApiResponse<BookingResponse> completeBooking(Integer id);
}
