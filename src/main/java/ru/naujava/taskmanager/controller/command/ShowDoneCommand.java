package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.service.TaskService;

/**
 * Команда для отображения списка выполненных задач пользователя.
 *
 * @author Seraph-coder
 * @since 18.12.2025
 */
@Component
public class ShowDoneCommand implements BotCommand {
    private final TaskService taskService;

    /**
     * Конструктор команды показа выполненных задач.
     */
    public ShowDoneCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/showdone";
    }

    @Override
    public CommandResponse execute(String command, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(
                    BotConstants.MSG_UNKNOWN_USER,
                    Action.NONE, null, KeyboardType.NONE);
        }
        String taskList = taskService.formatCompletedTaskAsString(chatId);
        return new CommandResponse(taskList, Action.NONE, null, KeyboardType.MAIN_MENU);
    }
}
