package com.central.api_gateway.controller;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public ResponseEntity<Map<String, Object>> globalFallback(ServerWebExchange exchange) {

        // 1. Initialize response map
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "SERVICE_UNAVAILABLE");

        // 2. Extract the Route to find the Service ID
        // The Gateway adds the matched Route object to the exchange attributes
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        String serviceName = "Unknown Service";
        if (route != null) {
            // This gets the 'id' from your yaml (e.g., 'reward-service', 'wallet-service')
            serviceName = route.getId();
        }

        // 3. Construct the dynamic message
        response.put("failedService", serviceName);
        response.put("message", "The " + serviceName + " is currently unavailable. Please try again later.");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}