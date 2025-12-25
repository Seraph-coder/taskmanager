package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TaskService;


/**
 * Команда для удаления задачи по её номеру в списке.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class DeleteTaskCommand implements BotCommand {
    private final TaskService taskService;

    /**
     * Конструктор удаления задачи.
     */
    public DeleteTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/delete";
    }

    @Override
    public CommandResponse execute(String taskId, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(
                    BotConstants.MSG_UNKNOWN_USER,
                    Action.NONE, null, KeyboardType.NONE);
        }

        String taskList = taskService.formatTaskListForDeletion(chatId);
        if (BotConstants.MSG_TASKS_EMPTY.equals(taskList)) {
            return new CommandResponse(taskList, Action.NONE, UserState.DEFAULT, KeyboardType.MAIN_MENU);
        }

        return new CommandResponse(
                "Выберите задачу для удаления:\n" + taskList + "\n\n" + BotConstants.MSG_ENTER_TASK_NUMBER_DELETE,
                Action.NONE,
                UserState.AWAITING_TASK_ID_FOR_DELETION,
                KeyboardType.CANCEL);
    }
}
