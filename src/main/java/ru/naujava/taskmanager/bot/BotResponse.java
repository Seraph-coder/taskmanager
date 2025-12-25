package ru.naujava.taskmanager.bot;

import ru.naujava.taskmanager.bot.dto.Keyboard;
import ru.naujava.taskmanager.controller.Action;

/**
 * Ответ бота.
 *
 * @param chatId   ID чата
 * @param text     Текст сообщения
 * @param keyboard Клавиатура (может быть null)
 * @param action   Действие, которое должен выполнить бот (например, отправить документ)
 */
public record BotResponse(long chatId, String text, Keyboard keyboard, Action action) {
}