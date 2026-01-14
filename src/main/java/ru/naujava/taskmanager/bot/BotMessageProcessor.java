package ru.naujava.taskmanager.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.keyboard.KeyboardProvider;
import ru.naujava.taskmanager.keyboard.model.Keyboard;
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
    private final KeyboardProvider keyboardProvider;
    private final MessageRateLimiter rateLimiter;
    private final Logger log = LoggerFactory.getLogger(BotMessageProcessor.class);

    /**
     * Конструктор процессора.
     */
    public BotMessageProcessor(StateMachine stateMachine,
                               KeyboardProvider keyboardProvider,
                               MessageRateLimiter rateLimiter) {
        this.stateMachine = stateMachine;
        this.keyboardProvider = keyboardProvider;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Обрабатывает callback.
     */
    public List<BotResponse> processCallback(Long chatId, String data) {
        log.debug("Обработка callback от chatId: {}", chatId);
        return processTextMessage(chatId, data);
    }

    /**
     * Обрабатывает текстовое сообщение.
     */
    public List<BotResponse> processTextMessage(Long chatId, String text) {
        if (rateLimiter.isRateLimited(chatId)) {
            log.debug("Лимит сообщений исчерпан для chatId: {}", chatId);
            return List.of(new BotResponse(chatId,
                    "Слишком много сообщений. Подождите минуту.", null));
        }
        log.debug("Обработка текстового сообщения от chatId: {}", chatId);

        List<BotResponse> responses = new ArrayList<>();
        try {
            StateTransition transition = stateMachine.processMessage(chatId, text);
            Keyboard keyboard = keyboardProvider.getKeyboard(transition.keyboardType());
            responses.add(new BotResponse(chatId, transition.responseText(), keyboard));

        } catch (Exception e) {
            log.error("Ошибка при обработки сообщения для chatId={}: {}", chatId, e.getMessage(), e);
            responses.add(new BotResponse(chatId,
                    "Произошла внутренняя ошибка. Попробуйте позже.", null));
        }

        return responses;
    }
}
