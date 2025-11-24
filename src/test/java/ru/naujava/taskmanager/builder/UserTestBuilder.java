package ru.naujava.taskmanager.builder;

import ru.naujava.taskmanager.entity.User;

/**
 * Билдер для создания объектов User в тестах.
 *
 * @author Seraph-coder
 * @since 14.11.2025
 */
public class UserTestBuilder {
    private long id;
    private long telegramId;

    /**
     * Устанавливает ID пользователя.
     */
    public UserTestBuilder withId(long id) {
        this.id = id;
        return this;
    }

    /**
     * Устанавливает Telegram ID пользователя.
     */
    public UserTestBuilder withTelegramId(long telegramId) {
        this.telegramId = telegramId;
        return this;
    }

    /**
     * Строит и возвращает объект User с заранее установленными полями.
     */
    public User build() {
        User user = new User();
        user.setId(id);
        user.setTelegramId(telegramId);
        return user;
    }
}
