package vn.kurisu.mentormatch.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.kurisu.mentormatch.dto.request.BookingRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.VNPayPaymentResponse;
import vn.kurisu.mentormatch.service.BookingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final BookingService bookingService;

    @PostMapping("/payments/vnpay/booking")
    public ApiResponse<VNPayPaymentResponse> createVNPayBooking(@RequestBody @Valid BookingRequest request) {
        return bookingService.createBookingWithVNPay(request);
    }

    @GetMapping("/payment/vnpay-return")
    public void handleVnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> vnpParams = new HashMap<>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {
                vnpParams.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        String redirectUrl = bookingService.handleVNPayReturn(vnpParams);
        response.sendRedirect(redirectUrl);
    }
}

