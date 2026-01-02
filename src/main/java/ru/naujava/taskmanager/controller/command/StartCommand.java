package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;

/**
 * Команда /start.
 *
 * @author Seraph-coder
 * @since 25.12.2025
 */
@Component
public class StartCommand implements BotCommand {
    @Override
    public String getCommandName() {
        return "/start";
    }

    @Override
    public CommandResponse execute(String command, Long chatId) {
        return new CommandResponse(BotConstants.MSG_WELCOME, null, KeyboardType.MAIN_MENU);
    }
}
