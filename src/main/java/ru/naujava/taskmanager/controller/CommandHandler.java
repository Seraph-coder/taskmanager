package ru.naujava.taskmanager.controller;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.controller.command.BotCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Обработчик команд для управления задачами.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Component
public class CommandHandler {
    private final Map<String, BotCommand> commands = new HashMap<>();

    /**
     * Конструктор принимает список команд и регистрирует их по имени.
     */
    public CommandHandler(List<BotCommand> commandsList) {
        for (BotCommand c : commandsList) {
            commands.put(c.getCommandName().toLowerCase(), c);
        }
    }

    /**
     * Обрабатывает входящее сообщение от пользователя.
     */
    public String handle(String messageFromUser, Long chatId) {
        if (messageFromUser == null || messageFromUser.isBlank()) {
            return "Пустое сообщение";
        }
        String trimmed = messageFromUser.trim();
        String[] parts = trimmed.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";
        return Optional.ofNullable(commands.get(cmd))
                .map(c -> c.execute(args, chatId))
                .orElse("Неизвестная команда. Введите /help для списка команд");
    }
}