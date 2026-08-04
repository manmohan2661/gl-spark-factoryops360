package com.factoryops.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        log.info("Incoming Request -> {} {}", method, path);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {

                    long endTime = System.currentTimeMillis();

                    int status = exchange.getResponse()
                            .getStatusCode()
                            .value();

                    log.info(
                            "Outgoing Response -> Status: {} | Time Taken: {} ms",
                            status,
                            (endTime - startTime)
                    );
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}