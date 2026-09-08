package com.payflow.payment.feign;

import com.payflow.payment.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(
        name = "merchant-service",
        configuration = FeignConfig.class,
        path = "/internal/merchants"
)
public interface MerchantServiceClient {

    @GetMapping("/{merchantId}")
    Map<String, Object> getMerchants(@PathVariable("merchantId") String merchantId);

    @GetMapping("/{merchantId}/validate")
    Map<String, Object> validateMerchant(@PathVariable("merchantId") String merchantId);
}
