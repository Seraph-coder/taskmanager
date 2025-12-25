package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;

/**
 * Команда для отмены текущего действия.
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
@Component
public class CancelCommand implements BotCommand {

    @Override
    public String getCommandName() {
        return "/cancel";
    }

    @Override
    public CommandResponse execute(String message, Long chatId) {
        return new CommandResponse(BotConstants.MSG_ACTION_CANCELLED,
                Action.NONE, null, KeyboardType.MAIN_MENU);
    }
}
