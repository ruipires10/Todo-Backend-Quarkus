package es.torres.repository;

import java.util.List;

import es.torres.entitiy.Todo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TodoRepository implements PanacheRepository<Todo> {
    // Find all completed todos
    public List<Todo> findCompleted() {
        return find("completed", true).list();
    }

    // Find todos by title (case-insensitive search)
    public List<Todo> findByTitle(String title) {
        return find("LOWER(title) LIKE LOWER(?1)", "%" + title + "%").list();
    }

    public List<Object[]> getTodoStatistics() {
        return getEntityManager().createNativeQuery(
                        "SELECT completed, COUNT(*) FROM todo GROUP BY completed")
                .getResultList();
    }
}
