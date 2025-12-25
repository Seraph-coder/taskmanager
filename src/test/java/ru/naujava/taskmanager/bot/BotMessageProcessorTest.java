package ru.naujava.taskmanager.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.service.TelegramIdStateService;

import java.util.List;

/**
 * Интеграционные тесты для {@link BotMessageProcessor}.
 * <p>
 * Косвенно тестируемые классы: StateMachine, KeyboardService, TelegramIdStateService,
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
    private TelegramIdStateService telegramIdStateService;

    /**
     * Проверяет успешную обработку команды /start.
     * Ожидается приветственное сообщение и основная клавиатура.
     */
    @Test
    void processTextMessage_StartCommand_Success() {
        long chatId = 1L;
        String text = "/start";

        List<BotResponse> responses = botMessageProcessor.processTextMessage(chatId, text);

        Assertions.assertEquals(1, responses.size());
        BotResponse response = responses.getFirst();
        Assertions.assertEquals(chatId, response.chatId());
        Assertions.assertEquals("Добро пожаловать в Task Manager Bot! Введите /help для списка команд", response.text());
        Assertions.assertNotNull(response.keyboard());
        Assertions.assertEquals(Action.NONE, response.action());
        Assertions.assertEquals(UserState.DEFAULT, telegramIdStateService.getOrCreateUserState(chatId));
    }

    /**
     * Проверяет успешную обработку callback-запроса на добавление задачи.
     * Ожидается изменение состояния на AWAITING_TASK_DESCRIPTION.
     */
    @Test
    void processCallback_Add_Success() {
        long chatId = 1L;
        // Эмулируем /start, чтобы создать пользователя
        botMessageProcessor.processTextMessage(chatId, "/start");

        List<BotResponse> responses = botMessageProcessor.processCallback(chatId, BotConstants.CALLBACK_ADD);

        Assertions.assertEquals(1, responses.size());
        BotResponse response = responses.getFirst();
        Assertions.assertEquals(chatId, response.chatId());
        Assertions.assertEquals(BotConstants.MSG_ENTER_TASK_DESCRIPTION, response.text());
        Assertions.assertNotNull(response.keyboard());
        Assertions.assertEquals(Action.NONE, response.action());
        Assertions.assertEquals(UserState.AWAITING_TASK_DESCRIPTION, telegramIdStateService.getOrCreateUserState(chatId));
    }

    /**
     * Проверяет обработку неизвестной команды.
     */
    @Test
    void processTextMessage_UnknownCommand() {
        long chatId = 1L;
        String text = "/unknown";
        botMessageProcessor.processTextMessage(chatId, "/start"); // Создаем пользователя

        List<BotResponse> responses = botMessageProcessor.processTextMessage(chatId, text);

        Assertions.assertEquals(1, responses.size());
        BotResponse response = responses.getFirst();
        Assertions.assertEquals(chatId, response.chatId());
        Assertions.assertEquals("Неизвестная команда. Введите /help для списка команд", response.text());
        Assertions.assertNotNull(response.keyboard()); // Должна вернуться основная клавиатура
        Assertions.assertEquals(Action.NONE, response.action());
        Assertions.assertEquals(UserState.DEFAULT, telegramIdStateService.getOrCreateUserState(chatId));
    }
}
