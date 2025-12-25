package ru.naujava.taskmanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.state.MessageHandler;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Обработчик callback данных от inline-кнопок.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
@Component
public class CallbackHandler implements MessageHandler {
    private final TaskService taskService;
    private final Logger log = LoggerFactory.getLogger(CallbackHandler.class);

    /**
     * Конструктор обработчика.
     */
    public CallbackHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Обрабатывает callbackData от inline-кнопок.
     */
    @Override
    public StateTransition handle(Long chatId, String callbackData) {
        if (callbackData == null || callbackData.isBlank()) {
            return new StateTransition(BotConstants.MSG_EMPTY_CALLBACK,
                    null, KeyboardType.NONE, Action.NONE);
        }

        return switch (callbackData) {
            case BotConstants.CALLBACK_LIST -> {
                String text = taskService.formatTaskList(chatId);
                yield new StateTransition(text, null, KeyboardType.MAIN_MENU, Action.NONE);
            }
            case BotConstants.CALLBACK_ADD ->
                    new StateTransition(BotConstants.MSG_ENTER_TASK_DESCRIPTION, UserState.AWAITING_TASK_DESCRIPTION,
                            KeyboardType.CANCEL, Action.NONE);
            case BotConstants.CALLBACK_DONE -> {
                String taskList = taskService.formatUncompletedTaskAsString(chatId);
                if (BotConstants.MSG_TASKS_EMPTY.equals(taskList)) {
                    yield new StateTransition(taskList, UserState.DEFAULT,
                            KeyboardType.MAIN_MENU, Action.NONE);
                } else {
                    yield new StateTransition("Ваши задачи:\n" + taskList
                            + "\n\n" + BotConstants.MSG_ENTER_TASK_NUMBER_COMPLETE,
                            UserState.AWAITING_TASK_ID_FOR_COMPLETION, KeyboardType.CANCEL, Action.NONE);
                }
            }
            case BotConstants.CALLBACK_DELETE -> {
                String taskList = taskService.formatTaskListForDeletion(chatId);
                if (BotConstants.MSG_TASKS_EMPTY.equals(taskList)) {
                    yield new StateTransition(taskList, UserState.DEFAULT,
                            KeyboardType.MAIN_MENU, Action.NONE);
                } else {
                    yield new StateTransition("Выберите задачу для удаления:\n" + taskList
                            + "\n\n" + BotConstants.MSG_ENTER_TASK_NUMBER_DELETE,
                            UserState.AWAITING_TASK_ID_FOR_DELETION, KeyboardType.CANCEL, Action.NONE);
                }
            }
            case BotConstants.CALLBACK_SHOWDONE -> {
                String text = taskService.formatCompletedTaskAsString(chatId);
                yield new StateTransition(text, null, KeyboardType.MAIN_MENU, Action.NONE);
            }
            case BotConstants.CALLBACK_CANCEL ->
                    new StateTransition(BotConstants.MSG_ACTION_CANCELLED, UserState.DEFAULT,
                            KeyboardType.MAIN_MENU, Action.NONE);
            default -> {
                log.warn("Неизвестный callback: {} от пользователя {}", callbackData, chatId);
                yield new StateTransition("Неизвестный callback: " + callbackData,
                        null, KeyboardType.NONE, Action.NONE);
            }
        };
    }
}
