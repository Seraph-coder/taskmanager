package ru.naujava.taskmanager.bot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.naujava.taskmanager.bot.state.StateMachine;
import ru.naujava.taskmanager.controller.CommandHandler;
import ru.naujava.taskmanager.entity.UserStateEnum;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * TelegramBot реализует бота для Telegram,
 * который обрабатывает входящие сообщения и отвечает на них.
 * Поддерживает стандартные команды вида /command,
 * а также inline-кнопки и обработку CallbackQuery.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public class TelegramBot implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private final String botToken;
    private final TelegramClient telegramClient;
    private final CommandHandler commandHandler;
    private final StateMachine stateMachine;
    private final Logger log = Logger.getLogger(TelegramBot.class.getName());

    public TelegramBot(String botToken, CommandHandler commandHandler,
                       StateMachine stateMachine) {
        this.botToken = botToken;
        this.commandHandler = commandHandler;
        if (botToken == null || botToken.isBlank()) {
            this.telegramClient = null;
        } else {
            this.telegramClient = new OkHttpTelegramClient(botToken);
        }
        this.stateMachine = stateMachine;
    }

    /**
     * Обрабатывает входящие обновления от Telegram.
     */
    @Override
    public void consume(List<Update> list) {
        for (Update update : list) {
            try {
                if (update.hasCallbackQuery()) {
                    handleCallback(update.getCallbackQuery());
                } else if (update.hasMessage() && update.getMessage().hasText()) {
                    Long chatId = update.getMessage().getChatId();
                    String messageFromUser = update.getMessage().getText();
                    handleIncomingText(chatId, messageFromUser);
                }
            } catch (Exception e) {
                log.warning("Ошибка при обработке обновления: " + e.getMessage());
            }
        }
    }

    /**
     * Обрабатывает CallbackQuery от inline-кнопок.
     */
    private void handleCallback(CallbackQuery callback) {
        Long chatId = callback.getMessage().getChatId();
        String data = callback.getData();

        answerCallback(callback.getId());

        String replyHandler = commandHandler.handleCallback(data, chatId);

        switch (data) {
            case "ADD" -> {
                stateMachine.setState(chatId, UserStateEnum.AWAITING_TASK_DESCRIPTION);
                sendMessageWithCancel(chatId, replyHandler);
            }
            case "DELETE" -> {
                String tasks = commandHandler.handle("/todo", chatId);
                if (tasks == null || tasks.isBlank() || tasks.contains("Список задач пуст")) {
                    sendMessage(chatId, tasks == null ? "Список задач пуст" : tasks);
                    sendMainMenu(chatId);
                } else {
                    sendMessage(chatId, tasks);
                    stateMachine.setState(chatId, UserStateEnum.AWAITING_TASK_ID_FOR_DELETION);
                    sendMessageWithCancel(chatId, replyHandler);
                }
            }
            case "LIST" -> {
                if (replyHandler != null && !replyHandler.isBlank()) {
                    sendMessage(chatId, replyHandler);
                }
                sendMainMenu(chatId);
            }
            case "CANCEL" -> {
                stateMachine.reset(chatId);
                sendMainMenu(chatId);
            }
            default -> {
                sendMessage(chatId, "Неизвестные данные обратного вызова");
                sendMainMenu(chatId);
            }
        }
    }

    /**
     * Обрабатывает входящие текстовые сообщения.
     */
    private void handleIncomingText(Long chatId, String text) {
        Objects.requireNonNull(text);
        Optional<UserStateEnum> stateOpt = stateMachine.getState(chatId);
        UserStateEnum state = stateOpt.orElse(UserStateEnum.DEFAULT);

        switch (state) {
            case AWAITING_TASK_DESCRIPTION -> {
                String addCmd = "/add " + text.trim();
                String addReply = commandHandler.handle(addCmd, chatId);
                stateMachine.reset(chatId);
                if (addReply != null && !addReply.isBlank()) sendMessage(chatId, addReply);
                sendMainMenu(chatId);
            }
            case AWAITING_TASK_ID_FOR_DELETION -> {
                String deleteCmd = "/delete " + text.trim();
                String deleteReply = commandHandler.handle(deleteCmd, chatId);
                stateMachine.reset(chatId);
                if (deleteReply != null && !deleteReply.isBlank()) sendMessage(chatId, deleteReply);
                sendMainMenu(chatId);
            }
            default -> {
                String reply = commandHandler.handle(text, chatId);
                if (reply != null && !reply.isBlank()) {
                    sendMessage(chatId, reply);
                    sendMainMenu(chatId);
                }
            }
        }
    }

    /**
     * Отправляет главное меню с inline-кнопками.
     */
    private void sendMainMenu(Long chatId) {
        InlineKeyboardButton add = InlineKeyboardButton.builder()
                .text("Добавить задачу").callbackData("ADD").build();
        InlineKeyboardButton del = InlineKeyboardButton.builder()
                .text("Удалить задачу").callbackData("DELETE").build();
        InlineKeyboardButton list = InlineKeyboardButton.builder()
                .text("Список задач").callbackData("LIST").build();

        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(add);
        row.add(del);
        row.add(list);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));

        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text("Выберите действие:")
                .replyMarkup(markup)
                .build();

        executeSafe(msg);
    }

    /**
     * Отправляет сообщение с кнопкой "Отменить".
     */
    private void sendMessageWithCancel(Long chatId, String text) {
        InlineKeyboardButton cancel = InlineKeyboardButton.builder()
                .text("Отменить").callbackData("CANCEL").build();
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(cancel);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));

        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();

        executeSafe(msg);
    }

    /**
     * Отправляет простое текстовое сообщение.
     */
    private void sendMessage(Long chatId, String reply) {
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(reply)
                .build();
        executeSafe(sendMessage);
    }

    /**
     * Безопасно выполняет отправку сообщения, обрабатывая исключения.
     */
    private void executeSafe(SendMessage msg) {
        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            log.warning("Не удалось отправить сообщение: " + e.getMessage());
        }
    }

    /**
     * Отвечает на CallbackQuery.
     */
    private void answerCallback(String callbackId) {
        try {
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackId)
                    .build();
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.warning("Не удалось ответить на callback query: " + e.getMessage());
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }
}