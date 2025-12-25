package ru.naujava.taskmanager.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.naujava.taskmanager.bot.dto.Keyboard;
import ru.naujava.taskmanager.bot.keyboard.KeyboardFactory;
import ru.naujava.taskmanager.bot.keyboard.KeyboardService;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.state.StateMachine;
import ru.naujava.taskmanager.state.StateTransition;

import java.util.ArrayList;
import java.util.List;

/**
 * Процессор сообщений бота.
 * Обрабатывает логику обработки обновлений и возвращает ответы для отправки.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
@Component
public class BotMessageProcessor {
    private final StateMachine stateMachine;
    private final KeyboardService keyboardService;
    private final MessageRateLimiter rateLimiter;
    private final KeyboardFactory keyboardFactory;
    private final Logger log = LoggerFactory.getLogger(BotMessageProcessor.class);

    /**
     * Конструктор процессора.
     */
    public BotMessageProcessor(StateMachine stateMachine,
                               KeyboardService keyboardService,
                               MessageRateLimiter rateLimiter,
                               KeyboardFactory keyboardFactory) {
        this.stateMachine = stateMachine;
        this.keyboardService = keyboardService;
        this.rateLimiter = rateLimiter;
        this.keyboardFactory = keyboardFactory;
    }

    /**
     * Обрабатывает callback.
     */
    public List<BotResponse> processCallback(Long chatId, String data) {
        log.info("Обработка callback от chatId: {}", chatId);
        return processTextMessage(chatId, data);
    }

    /**
     * Обрабатывает текстовое сообщение.
     */
    public List<BotResponse> processTextMessage(Long chatId, String text) {
        if (rateLimiter.isRateLimited(chatId)) {
            log.warn("Rate limit exceeded for chatId: {}", chatId);
            return List.of(new BotResponse(chatId,
                    "Слишком много сообщений. Подождите минуту.", null, Action.NONE));
        }
        log.info("Обработка текстового сообщения от chatId: {}", chatId);

        List<BotResponse> responses = new ArrayList<>();
        try {
            StateTransition transition = stateMachine.processMessage(chatId, text);
            Keyboard keyboardDto = switch (transition.keyboardType()) {
                case MAIN_MENU -> keyboardService.buildMainMenu();
                case CANCEL -> keyboardService.buildCancelKeyboard();
                case NONE -> null;
            };
            InlineKeyboardMarkup keyboard = keyboardFactory.build(keyboardDto);
            responses.add(new BotResponse(chatId, transition.responseText(), keyboard,
                    transition.action()));

        } catch (Exception e) {
            log.warn("Ошибка при обработки сообщения для chatId={}: {}", chatId, e.getMessage(), e);
            responses.add(new BotResponse(chatId,
                    "Произошла внутренняя ошибка. Попробуйте позже.", null, Action.NONE));
        }

        return responses;
    }
}
