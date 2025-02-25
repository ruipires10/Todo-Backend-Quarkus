package es.torres.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.torres.dto.InputTodoDto;
import es.torres.dto.OutputTodoDto;
import es.torres.dto.OutputTodoStatisticsDTO;
import es.torres.entitiy.Todo;
import es.torres.repository.TodoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

@ApplicationScoped
public class TodoService {

    @ConfigProperty(name = "scheme")
    String scheme;

    @Inject
    TodoRepository todoRepository;

    @Timed(name = "todo_get_all_time", description = "Time needed to get all todos")
    @Counted(name = "todo_get_all_requests", description = "Number of requests to get all todos")
    public List<OutputTodoDto> getAll() {
        final List<OutputTodoDto> outputTodoDtos = new ArrayList<>();
        final List<Todo> todos = todoRepository.listAll();
        for(Todo todo : todos) {
            outputTodoDtos.add(mapEntityToDto(todo));
        }
        return outputTodoDtos;
    }

    @Counted(name = "todo_requests_add", description = "Number of requests to add a todo")
    public OutputTodoDto createTodo(final InputTodoDto inputTodo, final UriInfo uriInfo) throws MalformedURLException {
        final Todo newTodo = mapDtoToEntity(inputTodo);
        newTodo.setUrl(uriInfo.getAbsolutePathBuilder().scheme(scheme).build().toURL());
        newTodo.persist();
        return mapEntityToDto(newTodo);
    }

    @Counted(name = "todo_requests_delete_all", description = "Number of requests to delete all todos")
    public void deleteAll() {
        todoRepository.deleteAll();
    }

    @Counted(name = "todo_requests_delete_one", description = "Number of requests to delete a todo")
    public void deteleById(Long id) {
        final Optional<Todo> todo = Todo.findByIdOptional(id);
        todo.ifPresentOrElse(todoRepository::delete, () -> {
            throw new NotFoundException("Todo does not exist!");
        });
    }

    @Counted(name = "todo_requests_get_one", description = "Number of requests to get a todo")
    public OutputTodoDto getById(Long id) {
        final Optional<Todo> optional = todoRepository.findByIdOptional(id);
        final Todo todo = optional.orElseThrow(() -> new NotFoundException("Todo does not exist!"));
        return mapEntityToDto(todo);
    }


    @Counted(name = "todo_requests_get_by_title", description = "Number of requests to get a todo by title")
    public List<OutputTodoDto> getByTitle(String needle) {
        return todoRepository.findByTitle(needle)
                .stream().map(this::mapEntityToDto)
                .toList();
    }

    @Timed(name = "todo_edit_time", description = "Time needed to edit a todo")
    @Counted(name = "todo_requests_edit", description = "Number of requests to edit a todo")
    public OutputTodoDto editById(Long id, InputTodoDto updates) {
        final Optional<Todo> optional = todoRepository.findByIdOptional(id);
        final Todo byId = optional.orElseThrow(() -> new NotFoundException("Todo does not exist!"));
        merge(byId, updates);
        todoRepository.persist(byId);
        return mapEntityToDto(byId);
    }

    public List<OutputTodoStatisticsDTO> getStatistics() {
        final List<Object[]> statistics = todoRepository.getTodoStatistics();
        System.out.println("Is Empty" + statistics.isEmpty() + " Size: " + statistics.size());
        statistics.stream().forEach(objects -> {
            System.out.println("Completed: " + objects[0] + " Count: " + objects[1]);
        });
        final List<OutputTodoStatisticsDTO> outputTodoStatisticsDTOS = new ArrayList<>();
        if (statistics.isEmpty()) {
            return outputTodoStatisticsDTOS;
        }
        statistics.forEach(stat -> {
            if (stat[0] == null || stat[1] == null) {
                return;
            }
            final OutputTodoStatisticsDTO outputTodoStatisticsDTO =
                    new OutputTodoStatisticsDTO((Boolean) stat[0], (Long) stat[1]);
            outputTodoStatisticsDTOS.add(outputTodoStatisticsDTO);
        });

        return outputTodoStatisticsDTOS;
    }

    private void merge(Todo current, InputTodoDto todoItem) {
        current.setTitle((String) (getLatest(current.getTitle(), todoItem.getTitle())));
        current.setCompleted(((Boolean) getLatest(current.getCompleted(), todoItem.getCompleted())));
        current.setOrder(((Integer) getLatest(current.getOrder(), todoItem.getOrder())));
    }

    private Object getLatest(Object old, Object latest) {
        return latest == null ? old : latest;
    }

    private Todo mapDtoToEntity(final InputTodoDto inputTodoDto){
        final Todo todo = new Todo();
        todo.setCompleted(inputTodoDto.getCompleted());
        todo.setTitle(inputTodoDto.getTitle());
        todo.setOrder(inputTodoDto.getOrder());

        return todo;
    }

    private OutputTodoDto mapEntityToDto(final Todo todo) {
        final OutputTodoDto outputTodoDto = new OutputTodoDto();
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
