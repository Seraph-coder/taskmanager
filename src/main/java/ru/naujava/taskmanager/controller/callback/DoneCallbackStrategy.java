package ru.naujava.taskmanager.controller.callback;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Стратегия для обработки callback отметки задачи как выполненной.
 *
 * @author Seraph-coder
 * @since 02.01.2026
 */
@Component
public class DoneCallbackStrategy implements CallbackStrategy {
    private final TaskService taskService;

    public DoneCallbackStrategy(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public StateTransition handle(Long chatId) {
        String taskList = taskService.formatUncompletedTaskAsString(chatId);
        if (BotConstants.MSG_TASKS_EMPTY.equals(taskList)) {
            return new StateTransition(
                    taskList,
                    UserState.DEFAULT,
                    KeyboardType.MAIN_MENU
            );
        }
        return new StateTransition(
                "Ваши задачи:\n" + taskList + "\n\n" + BotConstants.MSG_ENTER_TASK_NUMBER_COMPLETE,
                UserState.AWAITING_TASK_ID_FOR_COMPLETION,
                KeyboardType.CANCEL
        );
    }

    @Override
    public String getCallbackName() {
        return BotConstants.CALLBACK_DONE;
    }
}

