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
                .addRoute(new Route("/api/villains",
                        Origin.of("villain-backend", "http://villains-service-1:8080/villains"),
                        PathMode.PREFIX));
    }
}
