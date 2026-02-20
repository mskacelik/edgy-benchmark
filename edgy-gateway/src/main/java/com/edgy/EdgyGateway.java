package com.edgy;

import org.acme.edgy.runtime.api.Origin;
import org.acme.edgy.runtime.api.PathMode;
import org.acme.edgy.runtime.api.Route;
import org.acme.edgy.runtime.api.RoutingConfiguration;

import jakarta.enterprise.inject.Produces;

class EdgyGateway {
    @Produces
    RoutingConfiguration routing() {
        return new RoutingConfiguration()
                .addRoute(new Route("/api/villains/*",
                                Origin.of("villain-backend",
                                "http://villains-service-1:8080/villains/{__REQUEST_URI_AFTER_PREFIX__}"),
                        PathMode.PREFIX))
                .addRoute(new Route("/api/stork/villains/*",
                        Origin.of("villain-backends", "stork://villains-service/villains/{__REQUEST_URI_AFTER_PREFIX__}"),
                        PathMode.PREFIX))
                .addRoute(new Route("/api/heroes/*",
                        Origin.of("hero-backend", "https://heroes-service:8443/heroes/{__REQUEST_URI_AFTER_PREFIX__}"),
                        PathMode.PREFIX));
    }
}
