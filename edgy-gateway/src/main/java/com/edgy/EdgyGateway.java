package com.edgy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.acme.edgy.runtime.api.Origin;
import org.acme.edgy.runtime.api.PathMode;
import org.acme.edgy.runtime.api.Route;
import org.acme.edgy.runtime.api.RoutingConfiguration;
import org.acme.edgy.runtime.builtins.requests.RequestFaultToleranceApplier;
import org.acme.edgy.runtime.builtins.requests.RequestJsonObjectBodyModifier;
import org.acme.edgy.runtime.builtins.responses.ResponseJsonObjectBodyModifier;

import io.vertx.core.http.HttpMethod;
import jakarta.enterprise.inject.Produces;

class EdgyGateway {
    @Produces
    RoutingConfiguration routing() {
        return new RoutingConfiguration()
                        .addRoute(new Route("/edgy/villains/*",
                                                Origin.of("villain-backend",
                                                        "http://villains-service-1:8080/villains/{__REQUEST_URI_AFTER_PREFIX__}"),
                                        PathMode.PREFIX))
                .addRoute(new Route("/edgy/villains-post-transform",
                        Origin.of("villain-backend-with-request-and-response-payload-transformation",
                                "http://villains-service-1:8080/villains"),
                        PathMode.FIXED)
                        .addPredicate(rp -> HttpMethod.POST.equals(rp.request().method()))
                        .addRequestTransformer(new RequestJsonObjectBodyModifier(json -> {
                            // changes name to what ever it is to Green Goblin
                            json.put("name", "Green Goblin");
                            return json;
                        }))
                        .addResponseTransformer(new ResponseJsonObjectBodyModifier(json -> json
                                .put("response-processed-by-gateway-timestamp",
                                        Instant.now().toString()))))
                .addRoute(new Route("/edgy/stork/villains/*",
                        Origin.of("villain-backends", "stork://villains-service/villains/{__REQUEST_URI_AFTER_PREFIX__}"),
                        PathMode.PREFIX))
                .addRoute(new Route("/edgy/heroes/*",
                        Origin.of("hero-backend", "https://heroes-service:8443/heroes/{__REQUEST_URI_AFTER_PREFIX__}"),
                        PathMode.PREFIX))
                .addRoute(new Route("/edgy/villain-unstable-timeout-guard",
                        Origin.of("villain-unstable-backend",
                                "http://villains-service-1:8080/villains/unstable"),
                        PathMode.FIXED)
                        .addRequestTransformer(new RequestFaultToleranceApplier(
                                guardBuilder -> guardBuilder.withTimeout()
                                        .duration(5, ChronoUnit.SECONDS).done())))
                .addRoute(new Route("/edgy/villains-unstable-cb",
                        Origin.of("villain-unstable-backend",
                                "http://villains-service-1:8080/villains/unstable"),
                        PathMode.FIXED)
                        .addRequestTransformer(new RequestFaultToleranceApplier(
                                guardBuilder -> guardBuilder.withCircuitBreaker()
                                        .delay(10, ChronoUnit.SECONDS)
                                        .requestVolumeThreshold(4).failureRatio(0.5)
                                        .successThreshold(2).done()
                                        .withTimeout()
                                        .duration(1, ChronoUnit.SECONDS).done())))
                // order is important
                .addRoute(new Route("/edgy/am-i-villain",
                        Origin.of("iAmVillain", "http://villains-service-1:8080/villains/i-am-villain"),
                        PathMode.FIXED)
                        .addPredicate(rp -> "yes".equals(rp.request().getHeader("X-Is-Villain"))))
                .addRoute(new Route("/edgy/am-i-villain",
                        Origin.of("i-am-not-villain",
                                "http://villains-service-1:8080/villains/i-am-not-villain"),
                        PathMode.FIXED));

    }
}
