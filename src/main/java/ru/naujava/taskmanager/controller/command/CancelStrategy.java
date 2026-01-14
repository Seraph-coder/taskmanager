package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;

/**
 * Команда для отмены текущего действия.
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
@Component
public class CancelStrategy implements TextStrategy {

    @Override
    public String getCommandName() {
        return BotConstants.CANCEL_COMMAND;
    }

    @Override
    public CommandResponse execute(String message, Long chatId) {
        return new CommandResponse(BotConstants.MSG_ACTION_CANCELLED,
                null, KeyboardType.MAIN_MENU);
    }
}
