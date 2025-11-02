package ru.naujava.taskmanager.controller;

import org.springframework.stereotype.Component;

/**
 * Обработчик команд для управления задачами.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@Component
public class CommandHandler {
    private final CommandRegistry commandRegistry;

    public CommandHandler(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
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
        return commandRegistry.find(cmd)
                .map(c -> c.execute(args, chatId))
                .orElse("Неизвестная команда. Введите /help для списка команд");
    }
}

