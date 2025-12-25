package ru.naujava.taskmanager.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.naujava.taskmanager.controller.Action;

/**
 * Ответ бота.
 *
 * @param chatId   ID чата
 * @param text     Текст сообщения
 * @param keyboard Клавиатура (может быть null)
 * @param action   Действие, которое должен выполнить бот (например, отправить документ)
 */
public record BotResponse(long chatId, String text, InlineKeyboardMarkup keyboard, Action action) {
}