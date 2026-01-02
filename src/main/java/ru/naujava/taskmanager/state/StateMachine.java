package ru.naujava.taskmanager.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.controller.callback.CallbackStrategy;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TelegramIdStateService;

import java.util.*;
import java.util.stream.Collectors;

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
    private final Set<String> callbackCommands;
    private final Logger log = LoggerFactory.getLogger(StateMachine.class);
    private final MessageHandler defaultHandler;
    private final MessageHandler callbackHandler;

    /**
     * Конструктор стейтмашины.
     */
    public StateMachine(TelegramIdStateService telegramIdStateService,
                        List<MessageHandler> stateHandlers,
                        List<CallbackStrategy> callbackStrategies,
                        @Qualifier("callbackHandler") MessageHandler callbackHandler) {
        this.telegramIdStateService = telegramIdStateService;
        this.callbackHandler = callbackHandler;

        this.callbackCommands = callbackStrategies.stream()
                .map(CallbackStrategy::getCallbackName)
                .collect(Collectors.toSet());
        log.info("Зарегистрировано {} callback-команд в StateMachine: {}",
                callbackCommands.size(), callbackCommands);

        MessageHandler tempDefault = null;
        Set<UserState> seenStates = new HashSet<>();
        for (MessageHandler handler : stateHandlers) {
            Optional<UserState> stateOpt = handler.getHandledState();
            if (stateOpt.isPresent()) {
                UserState state = stateOpt.get();
                if (!seenStates.add(state)) {
                    throw new IllegalStateException("Duplicate handler for state: " + state);
                }
                this.handlers.put(state, handler);
                if (state == UserState.DEFAULT) {
                    tempDefault = handler;
                }
            }
        }
        this.defaultHandler = tempDefault;
        if (defaultHandler == null) {
            throw new IllegalStateException("No default handler found for UserState.DEFAULT");
        }
    }

    /**
     * Обрабатывает сообщение пользователя в зависимости от его текущего состояния.
     */
    public StateTransition processMessage(Long chatId, String text) {
        Objects.requireNonNull(chatId, "chatId не может быть null");
        Objects.requireNonNull(text, "text не может быть null");
        try {
            log.info("Обработка сообщения для chatId={}, текст: '{}'", chatId, text);
            UserState currentState = telegramIdStateService.getOrCreateUserState(chatId);

            MessageHandler handler;
            if (isCallback(text)) {
                handler = callbackHandler;
            } else if (text.startsWith("/")) {
                handler = defaultHandler; // CommandHandler
            } else {
                handler = handlers.getOrDefault(currentState, defaultHandler);
            }

            StateTransition transition = handler.handle(chatId, text);

            if (transition.newState() != null && transition.newState() != currentState) {
                log.info("Переход состояния для chatId={} из {} в {}", chatId, currentState, transition.newState());
                setState(chatId, transition.newState());
            }

            return transition;
        } catch (Exception e) {
            log.warn("Ошибка при обработке сообщения для chatId={}: {}", chatId, e.getMessage(), e);
            return new StateTransition(
                    "Произошла внутренняя ошибка. Попробуйте позже.", UserState.DEFAULT);
        }
    }

    /**
     * Проверяет, является ли текст callback-запросом.
     * Использует Set для O(1) проверки и соответствует принципу OCP:
     * новые callback-команды автоматически регистрируются через стратегии.
     */
    private boolean isCallback(String text) {
        return callbackCommands.contains(text);
    }

    /**
     * Устанавливает состояние пользователя.
     */
    private void setState(Long chatId, UserState state) {
        Objects.requireNonNull(chatId, "chatId не может быть null");
        Objects.requireNonNull(state, "state не может быть null");
        telegramIdStateService.changeUserState(chatId, state);
    }
}
