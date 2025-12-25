package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.entity.UserState;

/**
 * Команда для добавления новой задачи.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class AddTaskCommand implements BotCommand {

    /**
     * Конструктор добавления задачи.
     */
    public AddTaskCommand() {
    }

    @Override
    public String getCommandName() {
        return "/add";
    }

    @Override
    public CommandResponse execute(String description, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(
                    BotConstants.MSG_UNKNOWN_USER, Action.NONE, null, KeyboardType.NONE);
        }
        return new CommandResponse(BotConstants.MSG_ENTER_TASK_DESCRIPTION, Action.NONE,
                UserState.AWAITING_TASK_DESCRIPTION, KeyboardType.CANCEL);
    }
}