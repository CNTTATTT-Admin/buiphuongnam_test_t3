package vn.kurisu.mentormatch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.kurisu.mentormatch.dto.request.BookingRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.BookingResponse;
import vn.kurisu.mentormatch.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ApiResponse<BookingResponse> createBooking(@RequestBody @Valid BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping("/my-bookings")
    public ApiResponse<List<BookingResponse>> getMyTraineeBookings() {
        return bookingService.getMyTraineeBookings();
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<BookingResponse> completeBooking(@PathVariable Integer id) {
        return bookingService.completeBooking(id);
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<BookingResponse> cancelBooking(@PathVariable Integer id) {
        return bookingService.cancelBooking(id);
    }
}
