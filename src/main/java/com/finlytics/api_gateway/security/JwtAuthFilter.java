package com.finlytics.api_gateway.security;


import com.finlytics.api_gateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


@Component("JwtAuthFilter")
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtUtil jwtUtils;
    private static final String INTERNAL_KEY = "super-secret-123";

    public JwtAuthFilter(JwtUtil jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
        System.out.println("JwtAuthFilter initialized ✅");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            System.out.println("Token validated:");
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            String path = exchange.getRequest().getURI().getPath();
            System.out.println("Path :"+path);
            if (path.equals("/api/v1/user/login") || path.equals("/api/v1/user/register")) {
                return chain.filter(exchange);
            }
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            System.out.println("Token validated:"+token);
            if (!jwtUtils.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String userId = jwtUtils.extractUserId(token);
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate().header("X-User-Id", userId)
                            .header("X-Internal-Key",INTERNAL_KEY ).build())
                    .build();

            return chain.filter(modifiedExchange);
        };
    }

    public static class Config {
        // Configuration fields (if any) go here
    }
}