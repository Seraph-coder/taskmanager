package ru.naujava.taskmanager.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TaskService;

/**
 * Обработчик состояния AWAITING_TASK_DESCRIPTION.
 *
 * @author Seraph-coder
 * @since 12.12.2025
 */
@Component
public class AwaitingTaskDescriptionHandler implements MessageHandler, StateHandler {
    private final TaskService taskService;
    private final Logger log = LoggerFactory.getLogger(AwaitingTaskDescriptionHandler.class);

    /**
     * Конструктор.
     */
    public AwaitingTaskDescriptionHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public UserState getState() {
        return UserState.AWAITING_TASK_DESCRIPTION;
    }

    @Override
    public StateTransition handle(Long chatId, String text) {
        if ("/cancel".equalsIgnoreCase(text)) {
            return new StateTransition(BotConstants.MSG_ACTION_CANCELLED,
                    UserState.DEFAULT, KeyboardType.MAIN_MENU, Action.NONE);
        }
        try {
            taskService.createTask(text.trim(), chatId);
            return new StateTransition("Задача '" + text + "' добавлена",
                    UserState.DEFAULT, KeyboardType.MAIN_MENU, Action.NONE);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка при добавлении задачи для chatId={}: {}", chatId, e.getMessage(), e);
            return new StateTransition(e.getMessage() + "\n\n" +
                    BotConstants.MSG_ENTER_TASK_DESCRIPTION,
                    UserState.AWAITING_TASK_DESCRIPTION,
                    KeyboardType.CANCEL, Action.NONE);
        }
    }
}
