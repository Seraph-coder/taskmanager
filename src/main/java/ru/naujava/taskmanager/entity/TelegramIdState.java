package ru.naujava.taskmanager.entity;

import jakarta.persistence.*;

/**
 * Сущность для хранения состояния пользователя телеграм.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Entity
@Table(name = "telegram_id_states")
public class TelegramIdState {
    /**
     * Телеграм идентификатор пользователя.
     */
    @Id
    private Long telegramId;

    /**
     * Текущее состояние пользователя.
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private UserState state = UserState.DEFAULT;

    public TelegramIdState() {
    }

    public TelegramIdState(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}
