package com.payflow.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO representing a payment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String id;
    private String orderId;
    private String merchantId;
    private BigDecimal amount;
    private String currency;
    private String status;            // "AUTHORIZED", "CAPTURED", etc.
    private String paymentMethod;     // "CARD", "UPI", "NET_BANKING"
    private String authorizationCode; // Bank's approval code (null if not authorized)
    private String bankReferenceId;   // Bank's transaction ID (null if not authorized)
    private String failureReason;     // Why it failed (null if not failed)
    private Instant createdAt;
    private Instant updatedAt;
}