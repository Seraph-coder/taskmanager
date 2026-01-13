package ru.naujava.taskmanager.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.controller.callback.CallbackProcessor;
import ru.naujava.taskmanager.entity.UserState;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация стейтмашины для бота с использованием паттерна Стратегия.
 * Использует UserIdStateService для управления состояниями пользователей.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Component
public class StateMachine {
    private final UserIdStateService userIdStateService;
    private final Map<UserState, StateHandler> handlers;
    private final Logger log = LoggerFactory.getLogger(StateMachine.class);
    private final CallbackProcessor callbackHandler;

    /**
     * Конструктор стейтмашины.
     */
    public StateMachine(
            UserIdStateService userIdStateService,
            CallbackProcessor callbackHandler,
            List<StateHandler> stateHandlers) {

        this.userIdStateService = userIdStateService;
        this.callbackHandler = callbackHandler;
        this.handlers = stateHandlers.stream()
                .collect(Collectors.toMap(StateHandler::getHandledState, handler -> handler));
    }

    /**
     * Обрабатывает сообщение пользователя в зависимости от его текущего состояния.
     */
    public StateTransition processMessage(Long chatId, String text) {
        log.debug("Обработка сообщения для chatId={}, текст: '{}'", chatId, text);

        UserState currentState = userIdStateService.getUserState(chatId);
        if (currentState == null) {
            currentState = userIdStateService.createUserState(chatId);
        }
        StateTransition transition = routeMessage(chatId, text, currentState);

        updateStateIfChanged(chatId, transition, currentState);

        return transition;
    }

    /**
     * Маршрутизирует сообщение к соответствующему обработчику на основе текущего состояния пользователя.
     */
    private StateTransition routeMessage(Long chatId, String text, UserState currentState) {
        if (callbackHandler.isCallback(text)) {
            return callbackHandler.processCallback(text, chatId);
        }

        if (text.startsWith("/")) {
            return handlers.get(UserState.DEFAULT).handle(chatId, text);
        }

        StateHandler handler = handlers.getOrDefault(currentState, handlers.get(UserState.DEFAULT));
        return handler.handle(chatId, text);
    }

    /**
     * Обновляет состояние пользователя, если оно изменилось.
     */
    private void updateStateIfChanged(Long chatId, StateTransition transition, UserState currentState) {
        if (transition.newState() != null && !transition.newState().equals(currentState)) {
            log.debug("Переход состояния для chatId={} из {} в {}",
                    chatId, currentState, transition.newState());
            userIdStateService.changeUserState(chatId, transition.newState());
        }
    }
}
