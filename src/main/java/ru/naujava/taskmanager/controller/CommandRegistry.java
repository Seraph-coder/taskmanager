package ru.naujava.taskmanager.controller;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.controller.commands.BotCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реестр команд бота.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class CommandRegistry {
    private final Map<String, BotCommand> commands = new HashMap<>();

    /**
     * Конструктор реестра команд.
     */
    public CommandRegistry(List<BotCommand> commandsList) {
        for (BotCommand c : commandsList) {
            commands.put(c.getCommandName().toLowerCase(), c);
        }
    }

    /**
     * Найти команду по имени.
     */
    public Optional<BotCommand> find(String name) {
        return Optional.ofNullable(commands.get(name.toLowerCase()));
    }
}