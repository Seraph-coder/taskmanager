package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;

/**
 * Команда помощи.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class HelpCommand implements BotCommand {
    @Override
    public String getCommandName() {
        return "/help";
    }

    @Override
    public CommandResponse execute(String command, Long chatId) {
        return new CommandResponse(BotConstants.MSG_HELP, null, KeyboardType.MAIN_MENU);
    }
}
