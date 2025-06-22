package com.finlytics.api_gateway.security;

import com.finlytics.api_gateway.utils.JwtUtil;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

/*
spring-cloud-starter-gateway is built on top of Spring WebFlux, not Spring Web (MVC).
This means all security, filters, controllers, etc., in the Gateway must also be reactive.
since spring-cloud-gateway uses WebFlux, Spring Security expects reactive security components
ReactiveAuthenticationManager (instead of the MVC version)
ServerSecurityContextRepository (instead of SecurityContextHolder)
SecurityWebFilterChain (instead of SecurityFilterChain)
Methods and uses:
WebFilter instead of Filter
ServerHttpSecurity instead of HttpSecurity
SecurityWebFilterChain instead of SecurityFilterChain
ReactiveAuthenticationManager and ServerSecurityContextRepository

 */

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        if (JwtUtil.validateToken(token)) {
            String userId = JwtUtil.extractUserId(token);
            return Mono.just(new UsernamePasswordAuthenticationToken(userId, token, Collections.emptyList()));
        } else {
            return Mono.empty(); // Unauthorized
        }
    }
}