package com.example.ordercar.payme.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class PaymentUtil {
    @Value("${paycom.checkout.url}")
    private String checkoutUrl;

    @Value("${paycom.merchant.id}")
    private String merchantId;

    public String generatePaymentUrl(Integer orderId, Integer amount) {
        String str = String.format("m=%s;ac.order_id=%s;ac.key=2;a=%s", merchantId, orderId, amount);
        String s = Base64.getEncoder().encodeToString(str.getBytes());
        return checkoutUrl + s;
    }
}
