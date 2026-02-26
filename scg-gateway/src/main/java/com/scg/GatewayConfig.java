package com.scg;

import java.time.Duration;
import java.time.Instant;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.RouteMetadataUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private final ObjectMapper objectMapper;

    public GatewayConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("villain-backend", r -> r
                        .path("/scg/villains/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://villains-service-1:8080"))
                .route("villain-lb-backend", r -> r
                        .path("/scg/lb/villains", "/scg/lb/villains/**")
                        .filters(f -> f.rewritePath("/scg/lb(?<segment>/.*)", "${segment}"))
                        .uri("lb://villains-service"))
                .route("villain-unstable-timeout-guard", r -> r
                        .path("/scg/villain-unstable-timeout-guard")
                        .filters(f -> f.rewritePath("/scg/villain-unstable-timeout-guard", "/villains/unstable"))
                        .metadata(RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR, Duration.ofSeconds(5).toMillis())
                        .uri("http://villains-service-1:8080"))
                .route("villain-backend-with-request-and-response-payload-transformation", r -> r
                        .path("/scg/villains-post-transform")
                        .and().method(HttpMethod.POST)
                        .filters(f -> f
                                .rewritePath("/scg/villains-post-transform", "/villains")
                                .modifyRequestBody(String.class, String.class, MediaType.APPLICATION_JSON_VALUE,
                                        (exchange, body) -> {
                                            try {
                                                ObjectNode json = (ObjectNode) objectMapper.readTree(body);
                                                // changes name to whatever it is to Green Goblin
                                                json.put("name", "Green Goblin");
                                                return Mono.just(objectMapper.writeValueAsString(json));
                                            } catch (Exception e) {
                                                return Mono.just(body);
                                            }
                                        })
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, body) -> {
                                            try {
                                                ObjectNode json = (ObjectNode) objectMapper.readTree(body);
                                                json.put("response-processed-by-gateway-timestamp",
                                                        Instant.now().toString());
                                                return Mono.just(objectMapper.writeValueAsString(json));
                                            } catch (Exception e) {
                                                return Mono.just(body);
                                            }
                                        }))
                        .uri("http://villains-service-1:8080"))
                // order is important: predicate route first, fallback second
                .route("am-i-villain-is-villain", r -> r
                        .path("/scg/am-i-villain")
                        .and().header("X-Is-Villain", "yes")
                        .filters(f -> f.rewritePath("/scg/am-i-villain", "/villains/i-am-villain"))
                        .uri("http://villains-service-1:8080"))
                .route("am-i-villain-is-not-villain", r -> r
                        .path("/scg/am-i-villain")
                        .filters(f -> f.rewritePath("/scg/am-i-villain", "/villains/i-am-not-villain"))
                        .uri("http://villains-service-1:8080"))
                .build();
    }
}
