package es.torres;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import es.torres.dto.InputTodoDto;
import es.torres.dto.OutputTodoDto;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/todos")
public class TodoResource {

    @Inject
    TodoService todoService;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<OutputTodoDto>  getAll() {
        return todoService.getAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public OutputTodoDto create(InputTodoDto newTodo) throws MalformedURLException {
        return todoService.createTodo(newTodo);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response deleteAll() {
        todoService.deleteAll();
        return Response.ok().build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    @Transactional
    public Response deleteOne(@PathParam("id") Long id) {
       todoService.deteleById(id);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public OutputTodoDto getOne(@PathParam("id") Long id) {
       return todoService.getById(id);
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    @Transactional
    public OutputTodoDto edit(@PathParam("id") Long id, InputTodoDto updates) {
       return todoService.editById(id, updates);
    }
}