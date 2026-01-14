package ru.naujava.taskmanager.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность пользователя.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Entity
@Table(name = "users")
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный User ID пользователя.
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * Список задач, связанных с пользователем.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    public User() {
    }

    public User(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}