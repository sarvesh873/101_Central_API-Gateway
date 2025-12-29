package com.central.api_gateway.filter;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteFilter {

    public static final List<String> openApiEndpoints = List.of(
            "/api/user",
            "/api/auth/login",
            "/api/auth/refresh",
            "api/users/search"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
