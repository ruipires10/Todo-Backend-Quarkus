package es.torres.dto;

public class OutputTodoStatisticsDTO {
    private boolean completed;
    private long count;

    public OutputTodoStatisticsDTO(boolean completed, long count) {
        this.completed = completed;
        this.count = count;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getCount() {
        return count;
    }
}
