package ru.naujava.taskmanager.entity;

import jakarta.persistence.*;

/**
 * Сущность для хранения состояния пользователя.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Entity
@Table(name = "user_id_states")
public class UserIdState {
    /**
     * Идентификатор пользователя.
     */
    @Id
    private Long userId;

    /**
     * Текущее состояние пользователя.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState state = UserState.DEFAULT;

    public UserIdState() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}
