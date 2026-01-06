package ru.naujava.taskmanager.controller.callback;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Стратегия для обработки callback удаления задачи.
 *
 * @author Seraph-coder
 * @since 02.01.2026
 */
@Component
public class DeleteTaskCallbackStrategy implements CallbackStrategy {
    private final TaskService taskService;

    public DeleteTaskCallbackStrategy(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public StateTransition handle(Long chatId) {
        String taskList = taskService.formatTaskList(chatId);
        if (BotConstants.MSG_TASKS_EMPTY.equals(taskList)) {
            return new StateTransition(
                    taskList,
                    UserState.DEFAULT,
                    KeyboardType.MAIN_MENU
            );
        }
        return new StateTransition(
                "Ваши задачи:\n" + taskList + "\n\n" + BotConstants.MSG_ENTER_TASK_NUMBER,
                UserState.AWAITING_TASK_ID_FOR_DELETION,
                KeyboardType.CANCEL
        );
    }

    @Override
    public String getCallbackName() {
        return BotConstants.CALLBACK_DELETE;
    }
}
