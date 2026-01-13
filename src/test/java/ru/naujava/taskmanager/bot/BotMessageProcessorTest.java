package ru.naujava.taskmanager.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

/**
 * Интеграционные тесты для {@link BotMessageProcessor}.
 * <p>
 * Косвенно тестируемые классы: StateMachine, KeyboardService, UserIdStateService,
 * CommandHandler, CallbackHandler.
 * </p>
 *
 * @author Seraph-coder
 * @since 23.12.2025
 */
@SpringBootTest
@ActiveProfiles("test")
class BotMessageProcessorTest {

    @Autowired
    private BotMessageProcessor botMessageProcessor;

    @Autowired
    private KeyboardFactory keyboardFactory;

    /**
     * Проверяет успешную обработку команды /start.
     */
    @Test
    void processTextMessage_StartCommand_Success() {
        long chatId = 1L;
        String text = "/start";

        List<BotResponse> responses = botMessageProcessor.processTextMessage(chatId, text);

        Assertions.assertEquals(1, responses.size());
        BotResponse response = responses.getFirst();

        Assertions.assertEquals(chatId, response.chatId());
        Assertions.assertNotNull(response.text());

        InlineKeyboardMarkup markup = keyboardFactory.build(response.keyboard());
        Assertions.assertNotNull(markup);

        Assertions.assertEquals(4, markup.getKeyboard().size());
        Assertions.assertEquals("ADD", markup.getKeyboard().getFirst().getFirst().getCallbackData());
        Assertions.assertEquals("DELETE", markup.getKeyboard().getFirst().get(1).getCallbackData());
        Assertions.assertEquals("LIST", markup.getKeyboard().get(2).getFirst().getCallbackData());
        Assertions.assertEquals("SHOWDONE", markup.getKeyboard().get(3).getFirst().getCallbackData());
    }

    /**
     * Проверяет успешную обработку callback-запроса на добавление задачи.
     */
    @Test
    void processCallback_Add_Success() {
        long chatId = 1L;

        botMessageProcessor.processTextMessage(chatId, "/start");

        List<BotResponse> responses = botMessageProcessor.processCallback(chatId, BotConstants.CALLBACK_ADD);

        Assertions.assertEquals(1, responses.size());
        BotResponse response = responses.getFirst();

        Assertions.assertEquals(chatId, response.chatId());
        Assertions.assertEquals("Введите описание задачи", response.text());

        InlineKeyboardMarkup markup = keyboardFactory.build(response.keyboard());
        Assertions.assertNotNull(markup);

        Assertions.assertEquals(1, markup.getKeyboard().size());
        Assertions.assertEquals(1, markup.getKeyboard().getFirst().size());
        Assertions.assertEquals("CANCEL", markup.getKeyboard().getFirst().getFirst().getCallbackData());
    }

    /**
     * Проверяет обработку неизвестной команды.
     */
    @Test
    void processTextMessage_UnknownCommand() {
        long chatId = 1L;

        botMessageProcessor.processTextMessage(chatId, "/start");

        List<BotResponse> responses = botMessageProcessor.processTextMessage(chatId, "/unknown");

        Assertions.assertEquals(1, responses.size());
        BotResponse response = responses.getFirst();

        Assertions.assertEquals(chatId, response.chatId());
        Assertions.assertNotNull(response.text());

        InlineKeyboardMarkup markup = keyboardFactory.build(response.keyboard());
        Assertions.assertNotNull(markup);


        Assertions.assertEquals(4, markup.getKeyboard().size());
        Assertions.assertEquals("ADD", markup.getKeyboard().getFirst().getFirst().getCallbackData());
        Assertions.assertEquals("DELETE", markup.getKeyboard().getFirst().get(1).getCallbackData());
        Assertions.assertEquals("LIST", markup.getKeyboard().get(2).getFirst().getCallbackData());
        Assertions.assertEquals("SHOWDONE", markup.getKeyboard().get(3).getFirst().getCallbackData());
    }
}
