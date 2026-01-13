package ru.naujava.taskmanager.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;

/**
 * Обработчик состояния ожидания описания задачи.
 *
 * @author Seraph-coder
 * @since 12.12.2025
 */
@Component
public class AwaitingTaskDescriptionHandler implements StateHandler {
    private final TaskService taskService;
    private final Logger log = LoggerFactory.getLogger(AwaitingTaskDescriptionHandler.class);

    /**
     * Конструктор обработчика состояния ожидания описания задачи.
     */
    public AwaitingTaskDescriptionHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public UserState getHandledState() {
        return UserState.AWAITING_TASK_DESCRIPTION;
    }

    @Override
    public StateTransition handle(Long chatId, String text) {
        if (BotConstants.CANCEL_COMMAND.equalsIgnoreCase(text)) {
            return new StateTransition(BotConstants.MSG_ACTION_CANCELLED,
                    UserState.DEFAULT, KeyboardType.MAIN_MENU);
        }
        try {
            taskService.createTask(text.trim(), chatId);
            return new StateTransition("Задача '" + text + "' добавлена",
                    UserState.DEFAULT, KeyboardType.MAIN_MENU);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка при добавлении задачи для chatId={}: {}", chatId, e.getMessage(), e);
            return new StateTransition(e.getMessage() + "\n\n" +
                    BotConstants.MSG_ENTER_TASK_DESCRIPTION,
                    UserState.AWAITING_TASK_DESCRIPTION,
                    KeyboardType.CANCEL);
        }
    }
}
