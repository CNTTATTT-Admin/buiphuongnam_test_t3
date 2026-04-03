package vn.kurisu.mentormatch.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VNPayPaymentResponse {

    private BookingResponse booking;
    private String paymentUrl;
}

