package ru.naujava.taskmanager.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TaskService;

/**
 * Обработчик состояния AWAITING_TASK_ID_FOR_DELETION.
 *
 * @author Seraph-coder
 * @since 12.12.2025
 */
@Component
public class AwaitingTaskIdForDeletionHandler implements StateHandler, MessageHandler {
    private final Logger log = LoggerFactory.getLogger(AwaitingTaskIdForDeletionHandler.class);
    private final TaskService taskService;

    /**
     * Конструктор обработчика.
     */
    public AwaitingTaskIdForDeletionHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public UserState getState() {
        return UserState.AWAITING_TASK_ID_FOR_DELETION;
    }

    @Override
    public StateTransition handle(Long chatId, String text) {
        try {
            int taskIndex = Integer.parseInt(text.trim());
            Task toDelete = taskService.deleteTaskByIndexAndTelegramId(taskIndex, chatId);
            return new StateTransition(
                    "Задача “" + toDelete.getDescription() +
                            "” удалена", UserState.DEFAULT, true);
        } catch (NumberFormatException e) {
            log.warn("Неверный номер задачи для chatId={}: {}", chatId, text, e);
            return new StateTransition("Неверный номер задачи",
                    UserState.DEFAULT, true);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка при удалении задачи для chatId={}: {}", chatId, e.getMessage(), e);
            return new StateTransition(e.getMessage(), UserState.DEFAULT, true);
        }
    }
}
