package com.lazis.lazissultanagung.service;

import com.lazis.lazissultanagung.model.Billing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MidtransService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${midtrans.api.url}")
    private String midtransUrl;

    public String createPayment(Billing billing) {
        String url = midtransUrl + "/charge";

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("payment_type", "bank_transfer");
        request.put("transaction_details", Map.of(
                "order_id", "order-" + billing.getBillingId(),
                "gross_amount", billing.getBillingAmount()
        ));
        request.put("bank_transfer", Map.of("bank", "bca"));

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }
}
