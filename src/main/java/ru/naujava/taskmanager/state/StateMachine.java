package ru.naujava.taskmanager.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TelegramIdStateService;

import java.util.*;

/**
 * Реализация стейтмашины для Telegram бота с использованием паттерна Стратегия.
 * Использует TelegramIdStateService для управления состояниями пользователей.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Component
public class StateMachine {
    private final TelegramIdStateService telegramIdStateService;
    private final Map<UserState, MessageHandler> handlers = new HashMap<>();
    private final Map<UserState, Set<UserState>> allowedTransitions = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(StateMachine.class);

    /**
     * Конструктор стейтмашины.
     */
    public StateMachine(TelegramIdStateService telegramIdStateService,
                        List<MessageHandler> stateHandlers) {
        this.telegramIdStateService = telegramIdStateService;
        for (MessageHandler handler : stateHandlers) {
            if (handler instanceof StateHandler sh) {
                this.handlers.put(sh.getState(), handler);
            }
        }
        // Определение допустимых переходов
        allowedTransitions.put(UserState.DEFAULT,
                Set.of(UserState.AWAITING_TASK_DESCRIPTION, UserState.AWAITING_TASK_ID_FOR_DELETION));
        allowedTransitions.put(UserState.AWAITING_TASK_DESCRIPTION,
                Set.of(UserState.DEFAULT));
        allowedTransitions.put(UserState.AWAITING_TASK_ID_FOR_DELETION,
                Set.of(UserState.DEFAULT));
    }

    /**
     * Обрабатывает сообщение пользователя в зависимости от его текущего состояния.
     */
    public StateTransition processMessage(Long chatId, String text) {
        Objects.requireNonNull(chatId, "chatId не может быть null");
        Objects.requireNonNull(text, "text не может быть null");
        try {
            log.info("Обработка сообщения для chatId={}, текст: {}", chatId, text);
            UserState currentState = telegramIdStateService.getOrCreateUserState(chatId);
            MessageHandler handler = handlers.getOrDefault(currentState, handlers.get(UserState.DEFAULT));
            StateTransition transition = handler.handle(chatId, text);
            log.info("Текущее состояние: {}, Новое состояние: {}", currentState, transition.newState());
            if (transition.newState() != null && !transition.newState().equals(currentState)) {
                Set<UserState> allowed = allowedTransitions.getOrDefault(currentState, Set.of());
                if (allowed.contains(transition.newState())) {
                    setState(chatId, transition.newState());
                } else {
                    log.warn("Недопустимый переход из {} в {} для chatId={}",
                            currentState, transition.newState(), chatId);
                    return new StateTransition("Недопустимый переход состояния", currentState);
                }
            }
            return transition;
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при обработке сообщения для chatId={}: {}", chatId, e.getMessage(), e);
            return new StateTransition("Ошибка обработки состояния", null);
        } catch (IllegalStateException e) {
            log.error("Ошибка изменения состояния для chatId={}: {}", chatId, e.getMessage(), e);
            return new StateTransition("Ошибка изменения состояния", null);
        }
    }

    /**
     * Возвращает текущее состояние пользователя, если оно не DEFAULT.
     * Иначе возвращает пустой Optional.
     */
    public Optional<UserState> getState(Long chatId) {
        Objects.requireNonNull(chatId, "chatId не может быть null");
        try {
            UserState state = telegramIdStateService.getOrCreateUserState(chatId);
            return state == UserState.DEFAULT ? Optional.empty() : Optional.of(state);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка получения состояния для chatId={}: {}", chatId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Устанавливает состояние пользователя.
     */
    public void setState(Long chatId, UserState state) {
        Objects.requireNonNull(chatId, "chatId не может быть null");
        Objects.requireNonNull(state, "state не может быть null");
        telegramIdStateService.changeUserState(chatId, state);
    }

    /**
     * Сбрасывает состояние пользователя к DEFAULT.
     */
    public void resetState(Long chatId) {
        Objects.requireNonNull(chatId, "chatId не может быть null");
        telegramIdStateService.changeUserState(chatId, UserState.DEFAULT);
    }
}
