package es.torres;

import es.torres.dto.InputTodoDto;
import es.torres.dto.OutputTodoDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TodoService {

    @ConfigProperty(name = "scheme")
    String scheme;

    @Context
    UriInfo uriInfo;

    public List<OutputTodoDto> getAll() {
        List<OutputTodoDto> outputTodoDtos = new ArrayList<>();
        List<Todo> todos = Todo.listAll();
        for(Todo todo : todos) {
            outputTodoDtos.add(mapEntityToDto(todo));
        }
        return outputTodoDtos;
    }

    public OutputTodoDto createTodo(InputTodoDto inputTodo) throws MalformedURLException {
        Todo newTodo = mapDtoToEntity(inputTodo);
        newTodo.setUrl(uriInfo.getAbsolutePathBuilder().scheme(scheme).build().toURL());
        newTodo.persist();
        return mapEntityToDto(newTodo);
    }

    public void deleteAll() {
        Todo.deleteAll();
    }

    public void deteleById(Long id) {
        Todo todo = Todo.findById(id);
        todo.delete();
    }

    public OutputTodoDto getById(Long id) {
        Optional<Todo> optional = Todo.findByIdOptional(id);
        Todo todo = optional.orElseThrow(() -> new NotFoundException("Todo does not exist!"));
        return mapEntityToDto(todo);
    }

    public OutputTodoDto editById(Long id, InputTodoDto updates) {
        Optional<Todo> optional = Todo.findByIdOptional(id);
        Todo byId = optional.orElseThrow(() -> new NotFoundException("Todo does not exist!"));
        merge(byId, updates);
        return mapEntityToDto(byId);
    }

    private void merge(Todo current, InputTodoDto todoItem) {
        current.setTitle((String) (getLatest(current.getTitle(), todoItem.getTitle())));
        current.setCompleted(((Boolean) getLatest(current.getCompleted(), todoItem.getCompleted())));
        current.setOrder(((Integer) getLatest(current.getOrder(), todoItem.getOrder())));
    }

    private Object getLatest(Object old, Object latest) {
        return latest == null ? old : latest;
    }

    private Todo mapDtoToEntity(InputTodoDto inputTodoDto){
        Todo todo = new Todo();
        todo.setCompleted(inputTodoDto.getCompleted());
        todo.setTitle(inputTodoDto.getTitle());
        todo.setOrder(inputTodoDto.getOrder());

        return todo;
    }

    private OutputTodoDto mapEntityToDto(Todo todo) {
        OutputTodoDto outputTodoDto = new OutputTodoDto();
        outputTodoDto.setId(todo.getId());
        outputTodoDto.setCompleted(todo.getCompleted());
        outputTodoDto.setTitle(todo.getTitle());
        outputTodoDto.setOrder(todo.getOrder());
        try {
            outputTodoDto.setUrl(todo.getUrl().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return outputTodoDto;
    }
}
