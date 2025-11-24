package ru.naujava.taskmanager.builder;

import ru.naujava.taskmanager.entity.Task;

/**
 * Билдер для создания объектов Task в тестах.
 *
 * @author Seraph-coder
 * @since 18.11.2025
 */
public class TaskTestBuilder {
    private long id;
    private String description;

    /**
     * Устанавливает ID задачи.
     */
    public TaskTestBuilder withId(long id) {
        this.id = id;
        return this;
    }

    /**
     * Устанавливает описание задачи.
     */
    public TaskTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Строит и возвращает объект Task с заранее установленными полями.
     */
    public Task build() {
        Task task = new Task();
        task.setId(id);
        task.setDescription(description);
        return task;
    }
}
