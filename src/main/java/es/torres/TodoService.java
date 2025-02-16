package es.torres;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.MalformedURLException;
import java.util.Optional;

@ApplicationScoped
public class TodoService {

    @ConfigProperty(name = "scheme")
    String scheme;

    @Context
    UriInfo uriInfo;

    public Todo createTodo(Todo newTodo) throws MalformedURLException {
        newTodo.url = uriInfo.getAbsolutePathBuilder().scheme(scheme).build().toURL();
        newTodo.persist();
        return newTodo;
    }

    public void deleteAll() {
        Todo.deleteAll();
    }

    public void deteleById(Long id) {
        Todo todo = Todo.findById(id);
        todo.delete();
    }

    public Todo getById(Long id) {
        Optional<Todo> optional = Todo.findByIdOptional(id);
        Todo todo = optional.orElseThrow(() -> new NotFoundException("Todo does not exist!"));
        return todo;
    }

    public Todo editById(Long id, Todo updates) {
        Optional<Todo> optional = Todo.findByIdOptional(id);
        Todo byId = optional.orElseThrow(() -> new NotFoundException("Todo does not exist!"));
        byId.title = updates.title;
        byId.completed = updates.completed;
        byId.order = updates.order;

        return byId;
    }
}
