package vn.kurisu.mentormatch.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
    @Value("${vnpay.tmnCode}")
    public String vnp_TmnCode;

    @Value("${vnpay.secretKey}")
    public String secretKey;

    @Value("${vnpay.vnp_PayUrl}")
    public String vnp_PayUrl;

    @Value("${vnpay.vnp_ApiUrl}")
    public String vnp_ApiUrl;

    @Value("${vnpay.returnUrl}")
    public String vnp_ReturnUrl;

    // Hàm tạo chữ ký bảo mật (Hash SHA-512) chuẩn của VNPay
    public String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
}
