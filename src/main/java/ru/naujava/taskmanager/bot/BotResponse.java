package ru.naujava.taskmanager.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.naujava.taskmanager.controller.Action;

/**
 * Ответ бота.
 *
 * @param chatId   ID чата
 * @param text     Текст сообщения
 * @param keyboard Клавиатура (может быть null)
 * @param action   Действие
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
public record BotResponse(Long chatId, String text, InlineKeyboardMarkup keyboard, Action action) {
}
