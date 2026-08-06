package com.factoryops.analytics.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                requestTemplate.header("X-User-Name", authentication.getName());
                
                // Extract role without the "ROLE_" prefix to pass to downstream services
                authentication.getAuthorities().stream()
                        .findFirst()
                        .ifPresent(authority -> {
                            String role = authority.getAuthority().replace("ROLE_", "");
                            requestTemplate.header("X-User-Role", role);
                        });
            }
        };
    }
}
