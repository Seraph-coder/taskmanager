package ru.naujava.taskmanager.entity;

import jakarta.persistence.*;

/**
 * Сущность для хранения состояния пользователя.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Entity
@Table(name = "user_states")
public class UserState {
    @Id
    private Long telegramId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStateEnum state = UserStateEnum.DEFAULT;

    public UserState() {
    }

    public UserState(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public UserStateEnum getState() {
        return state;
    }

    public void setState(UserStateEnum state) {
        this.state = state;
    }
}
