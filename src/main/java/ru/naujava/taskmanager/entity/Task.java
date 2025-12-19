package ru.naujava.taskmanager.entity;

import jakarta.persistence.*;

/**
 * Сущность задачи.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Entity
@Table(name = "tasks")
public class Task {
    /**
     * Идентификатор задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Описание задачи.
     */
    @Column(nullable = false)
    private String description;

    /**
     * Статус выполнения задачи.
     */
    @Column(nullable = false)
    private boolean done = false;

    /**
     * Пользователь, которому принадлежит задача.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Task() {
    }

    public Task(String description, User user) {
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String title) {
        this.description = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}