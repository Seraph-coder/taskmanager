package ru.naujava.taskmanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.state.StateMachine;

/**
 * Обработчик callback данных от inline-кнопок.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
@Component
public class CallbackHandler {
    private final StateMachine stateMachine;
    private final TaskService taskService;
    private final Logger log = LoggerFactory.getLogger(CallbackHandler.class);

    /**
     * Конструктор обработчика.
     */
    public CallbackHandler(StateMachine stateMachine, TaskService taskService) {
        this.stateMachine = stateMachine;
        this.taskService = taskService;
    }

    /**
     * Обрабатывает callbackData от inline-кнопок.
     */
    public CommandResponse handle(String callbackData, Long chatId) {
        if (callbackData == null || callbackData.isBlank()) {
            return new CommandResponse(BotConstants.MSG_EMPTY_CALLBACK,
                    Action.NONE, null);
        }

        return switch (callbackData) {
            case BotConstants.CALLBACK_LIST -> {
                String text = taskService.formatTaskList(chatId);
                yield new CommandResponse(text, Action.NONE, null, true);
            }
            case BotConstants.CALLBACK_ADD ->
                    new CommandResponse(BotConstants.MSG_ENTER_TASK_DESCRIPTION, Action.SEND_CANCEL,
                            UserState.AWAITING_TASK_DESCRIPTION, false, null);
            case BotConstants.CALLBACK_DELETE ->
                    new CommandResponse(BotConstants.MSG_ENTER_TASK_NUMBER, Action.SEND_CANCEL,
                            UserState.AWAITING_TASK_ID_FOR_DELETION, false, null);
            case BotConstants.CALLBACK_CANCEL -> {
                UserState currentState = stateMachine.getState(chatId).orElse(UserState.DEFAULT);
                Action action = switch (currentState) {
                    case AWAITING_TASK_DESCRIPTION -> Action.CANCEL_ADD_TASK;
                    case AWAITING_TASK_ID_FOR_DELETION -> Action.CANCEL_DELETE_TASK;
                    default -> Action.NONE;
                };
                yield new CommandResponse(BotConstants.MSG_ACTION_CANCELLED, action,
                        null, true);
            }
            default -> {
                log.warn("Неизвестные callbackData '{}' от пользователя {}", callbackData, chatId);
                yield new CommandResponse(BotConstants.MSG_UNKNOWN_CALLBACK,
                        Action.NONE, null, true);
            }
        };
    }
}
