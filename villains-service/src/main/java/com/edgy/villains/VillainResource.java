package com.edgy.villains;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;

@Path("/villains")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VillainResource {

    @GET
    public Uni<List<Villain>> getAll() {
        return Villain.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Villain> getById(@PathParam("id") Long id) {
        return Villain.findById(id);
    }

    @GET
    @Path("/unstable")
    public Uni<List<Villain>> unstable() {
        if (ThreadLocalRandom.current().nextInt(100) < 5) {
            return Uni.createFrom().<List<Villain>>nothing()
                    .ifNoItem().after(Duration.ofSeconds(30))
                    .failWith(new WebApplicationException(Response.Status.REQUEST_TIMEOUT));
        }
        return Villain.listAll();
    }

    @POST
    @WithTransaction
    @ResponseStatus(StatusCode.CREATED)
    public Uni<Villain> create(Villain villain) {
        return villain.persist();
    }
}
