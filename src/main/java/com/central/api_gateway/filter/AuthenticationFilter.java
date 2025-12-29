package com.central.api_gateway.filter;

import com.central.api_gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config>implements Ordered {

    @Autowired
    private RouteFilter validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                // Check for Authorization header
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                
                // Check if header is missing or empty
                if (authHeader == null || authHeader.isEmpty()) {
                    log.warn("Missing Authorization header");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization Header");
                }
                
                // Extract token from Bearer scheme
                if (!authHeader.startsWith("Bearer ")) {
                    log.warn("Invalid Authorization header format. Expected 'Bearer <token>'");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header format");
                }
                
                // Get the token part after 'Bearer '
                authHeader = authHeader.substring(7);
                if (authHeader.isEmpty()) {
                    log.warn("Empty token provided");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Empty token provided");
                }
                try {
                    boolean isValidToken = jwtUtil.validateToken(authHeader);
                    if (isValidToken) {
                        // Inject userCode into headers for downstream gRPC services
                         String userCode = jwtUtil.extractUserCode(authHeader);
                        log.debug("Token validated successfully for user: {}", userCode);
                        return chain.filter(exchange.mutate()
                                .request(exchange.getRequest().mutate().header("X-User-Code", userCode).build())
                                .build());
                    } else {
                        log.warn("Received invalid or expired token");
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
                    }
                } catch (Exception e) {
                    log.error("Authentication failed: {}", e.getMessage(), e);
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to application", e);
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
    @Override
    public int getOrder() {
        return -1; // Ensures it runs BEFORE the gRPC filter
    }
}
