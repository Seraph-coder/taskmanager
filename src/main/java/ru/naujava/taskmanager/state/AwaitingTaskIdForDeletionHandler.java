package ru.naujava.taskmanager.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;

import java.util.Optional;


/**
 * Обработчик состояния ожидания номера задачи для удаления.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
@Component
public class AwaitingTaskIdForDeletionHandler implements MessageHandler {
    private final TaskService taskService;
    private final Logger log = LoggerFactory.getLogger(AwaitingTaskIdForDeletionHandler.class);

    /**
     * Конструктор.
     */
    public AwaitingTaskIdForDeletionHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public Optional<UserState> getHandledState() {
        return Optional.of(UserState.AWAITING_TASK_ID_FOR_DELETION);
    }

    @Override
    public StateTransition handle(Long chatId, String text) {
        if ("/cancel".equalsIgnoreCase(text)) {
            return new StateTransition(BotConstants.MSG_ACTION_CANCELLED,
                    UserState.DEFAULT, KeyboardType.MAIN_MENU);
        }
        try {
            int taskIndex = Integer.parseInt(text.trim());
            Task toDelete = taskService.deleteTaskByIndexFromCombinedList(taskIndex, chatId);
            return new StateTransition(
                    "Задача '" + toDelete.getDescription() +
                            "' удалена", UserState.DEFAULT, KeyboardType.MAIN_MENU, Action.NONE);
        } catch (NumberFormatException e) {
            log.warn("Неверный номер задачи для chatId={}: {}", chatId, text, e);
            return new StateTransition("Неверный номер задачи. Попробуйте еще раз.",
                    UserState.AWAITING_TASK_ID_FOR_DELETION, KeyboardType.CANCEL);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка при удалении задачи для chatId={}: {}", chatId, e.getMessage(), e);
            return new StateTransition(e.getMessage() + ". Попробуйте еще раз.",
                    UserState.AWAITING_TASK_ID_FOR_DELETION, KeyboardType.CANCEL);
        }
    }
}
