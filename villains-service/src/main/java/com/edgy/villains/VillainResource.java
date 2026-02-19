package com.edgy.villains;

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

@Path("/villains")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VillainResource {

    /** Retrieve all villains. */
    @GET
    public List<Villain> getAll() {
        return Villain.listAll();
    }

    /** Retrieve a single villain by id. */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Villain villain = Villain.findById(id);
        if (villain == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(villain).build();
    }

    /** Create a new villain. */
    @POST
    @Transactional
    public Response create(Villain villain) {
        villain.persist();
        return Response.status(Response.Status.CREATED).entity(villain).build();
    }
}
