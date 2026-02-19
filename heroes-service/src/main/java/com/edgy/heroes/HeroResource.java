package com.edgy.heroes;

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

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeroResource {

    @GET
    public List<Hero> getAll() {
        return Hero.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Hero hero = Hero.findById(id);
        if (hero == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(hero).build();
    }

    @POST
    @Transactional
    public Response create(Hero hero) {
        hero.persist();
        return Response.status(Response.Status.CREATED).entity(hero).build();
    }
}
