package ru.naujava.taskmanager.bot;

import ru.naujava.taskmanager.keyboard.model.Keyboard;

/**
 * Ответ бота.
 *
 * @param chatId   ID чата
 * @param text     Текст сообщения
 * @param keyboard Клавиатура (может быть null)
 */
public record BotResponse(long chatId, String text, Keyboard keyboard) {
}
