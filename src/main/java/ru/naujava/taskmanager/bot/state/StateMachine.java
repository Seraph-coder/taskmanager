package ru.naujava.taskmanager.bot.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.UserStateEnum;
import ru.naujava.taskmanager.service.UserStateService;

import java.util.Objects;
import java.util.Optional;

/**
 * Реализация стейтмашины для Telegram бота.
 * Использует UserStateService для управления состояниями пользователей.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Component
public class StateMachine {
    private final UserStateService userStateService;
    private final Logger log = LoggerFactory.getLogger(StateMachine.class);

    /**
     * Конструктор стейтмашины.
     */
    public StateMachine(UserStateService userStateService) {
        this.userStateService = userStateService;
    }

    /**
     * Возвращает текущее состояние пользователя по его chatId.
     * При ошибке возвращает Optional.empty(), логируя ошибку.
     */
    public Optional<UserStateEnum> getState(Long chatId) {
        Objects.requireNonNull(chatId, "chatId не может быть null");
        try {
            return Optional.ofNullable(userStateService.getOrCreateUserState(chatId));
        } catch (IllegalArgumentException e) {
            log.error("Не удалось получить состояние для chatId={}: {}", chatId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Устанавливает состояние пользователя по его chatId,
     * если у пользователя нет состояния, создает его.
     * Если пользователь с таким chatId не существует, логирует ошибку.
     */
    public void setState(Long chatId, UserStateEnum state) {
        Objects.requireNonNull(chatId, "telegramId не может быть null");
        Objects.requireNonNull(state, "state не может быть null");
        try {
            userStateService.changeUserState(chatId, state);
        } catch (IllegalArgumentException e) {
            log.error("Не удалось установить состояние {} для chatId={}: {}", state, chatId, e.getMessage(), e);
        }
    }

    /**
     * Сбрасывает состояние пользователя по его chatId в DEFAULT.
     * Если пользователь с таким chatId не существует, логирует ошибку.
     */
    public void reset(Long chatId) {
        setState(chatId, UserStateEnum.DEFAULT);
    }
}
