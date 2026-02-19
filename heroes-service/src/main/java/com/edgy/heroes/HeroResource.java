package com.edgy.heroes;

import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeroResource {

    @GET
    public Uni<List<Hero>> getAll() {
        return Hero.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Hero> getById(@PathParam("id") Long id) {
        return Hero.findById(id);
    }

    @POST
    @Transactional
    @ResponseStatus(StatusCode.CREATED)
    public Uni<Hero> create(Hero hero) {
        return hero.persist();
    }
}
