package ru.naujava.taskmanager.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CallbackHandler;
import ru.naujava.taskmanager.controller.CommandResponse;
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
    private final CallbackHandler callbackHandler;
    private final KeyboardBuilder keyboardBuilder;
    private final Logger log = LoggerFactory.getLogger(BotMessageProcessor.class);

    /**
     * Конструктор процессора.
     */
    public BotMessageProcessor(StateMachine stateMachine, CallbackHandler callbackHandler,
                               KeyboardBuilder keyboardBuilder) {
        this.stateMachine = stateMachine;
        this.callbackHandler = callbackHandler;
        this.keyboardBuilder = keyboardBuilder;
    }

    /**
     * Обрабатывает обновление и возвращает список ответов.
     */
    public List<BotResponse> processUpdate(Update update) {
        List<BotResponse> responses = new ArrayList<>();
        try {
            if (update.hasCallbackQuery()) {
                CallbackQuery callback = update.getCallbackQuery();
                Long chatId = callback.getMessage().getChatId();
                log.info("Обработка callback от chatId: {}", chatId);
                responses.addAll(processCallback(callback));
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                Long chatId = update.getMessage().getChatId();
                String text = update.getMessage().getText();
                log.info("Обработка текстового сообщения от chatId: {}", chatId);
                responses.addAll(processTextMessage(chatId, text));
            }
        } catch (Exception e) {
            log.warn("Ошибка при обработке обновления: {}", e.getMessage(), e);
        }
        return responses;
    }

    /**
     * Обрабатывает callback.
     */
    private List<BotResponse> processCallback(CallbackQuery callback) {
        List<BotResponse> responses = new ArrayList<>();
        Long chatId = callback.getMessage().getChatId();
        String data = callback.getData();

        CommandResponse response = callbackHandler.handle(data, chatId);
        responses.add(new BotResponse(chatId, response.text(), response.keyboard(), response.action()));

        if (response.shouldSendMenu()) {
            responses.add(new BotResponse(chatId, BotConstants.MSG_CHOOSE_ACTION,
                    keyboardBuilder.buildMainMenu(), Action.NONE));
        }

        switch (response.action()) {
            case SET_STATE -> {
                stateMachine.setState(chatId, response.newState());
            }
            case SEND_CANCEL -> {
                responses.add(new BotResponse(chatId, BotConstants.MSG_CHOOSE_ACTION,
                        keyboardBuilder.buildCancelKeyboard(),
                        Action.NONE));
                stateMachine.setState(chatId, response.newState());
            }
            case CANCEL_ADD_TASK, CANCEL_DELETE_TASK -> {
                stateMachine.resetState(chatId);
            }
            case NONE -> {
                // Нет дополнительных действий
            }
        }

        return responses;
    }

    /**
     * Обрабатывает текстовое сообщение.
     */
    private List<BotResponse> processTextMessage(Long chatId, String text) {
        List<BotResponse> responses = new ArrayList<>();
        StateTransition transition = stateMachine.processMessage(chatId, text);
        responses.add(new BotResponse(chatId, transition.responseText(), transition.keyboard(),
                transition.action()));

        if (transition.shouldSendMenu()) {
            responses.add(new BotResponse(chatId, BotConstants.MSG_CHOOSE_ACTION,
                    keyboardBuilder.buildMainMenu(), Action.NONE));
        }

        return responses;
    }
}
