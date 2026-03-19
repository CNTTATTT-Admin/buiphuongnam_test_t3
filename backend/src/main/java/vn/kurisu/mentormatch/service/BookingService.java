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

    ApiResponse<org.springframework.data.domain.Page<BookingResponse>> getMyTraineeBookings(org.springframework.data.domain.Pageable pageable, String status);

    ApiResponse<BookingResponse> cancelBooking(Integer id);

    ApiResponse<BookingResponse> completeBooking(Integer id);

    ApiResponse<vn.kurisu.mentormatch.dto.response.VNPayPaymentResponse> payExistingBooking(Integer bookingId);
}
