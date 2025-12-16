package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
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
                /add [описание] – добавить задачу
                /todo – показать список задач
                /delete [номер] – удалить задачу
                /help – справка по командам
                """, Action.NONE, null, true);
    }
}
