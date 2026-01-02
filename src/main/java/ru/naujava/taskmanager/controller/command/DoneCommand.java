package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;

/**
 * Команда для перехода в режим отметки задачи как выполненной.
 * @author Seraph-coder
 * @since 18.12.2025
 */
@Component
public class DoneCommand implements BotCommand {

    private final TaskService taskService;

    public DoneCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/done";
    }

    @Override
    public CommandResponse execute(String message, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(BotConstants.MSG_UNKNOWN_USER, null, KeyboardType.NONE);
        }

        String taskList = taskService.formatUncompletedTaskAsString(chatId);

        if (BotConstants.MSG_TASKS_EMPTY.equals(taskList)) {
            return new CommandResponse(taskList, UserState.DEFAULT, KeyboardType.MAIN_MENU);
        }

        return new CommandResponse(
                taskList,
                UserState.AWAITING_TASK_ID_FOR_COMPLETION,
                KeyboardType.CANCEL);
    }
}
