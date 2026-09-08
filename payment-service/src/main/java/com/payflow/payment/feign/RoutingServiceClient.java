package com.payflow.payment.feign;

import com.payflow.payment.config.FeignConfig;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(
        name = "routing-service",
        configuration = FeignConfig.class,
        path = "/internal"
)
public interface RoutingServiceClient {

    @PostMapping("/route")
    Map<String, Object> routePayment(@RequestBody Map<String, Object> request);


}
