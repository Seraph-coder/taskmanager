package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;

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
        return new CommandResponse("""
                Доступные команды:
                /add – начать добавление новой задачи
                /todo – показать список ваших задач
                /delete – начать удаление задачи
                /cancel – отменить текущее действие
                /help – показать эту справку
                """, Action.NONE, null, KeyboardType.MAIN_MENU);
    }
}
