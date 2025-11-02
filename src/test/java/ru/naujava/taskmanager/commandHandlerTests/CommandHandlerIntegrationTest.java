package ru.naujava.taskmanager.commandHandlerTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.naujava.taskmanager.bot.TelegramBot;
import ru.naujava.taskmanager.controller.CommandHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты интеграции CommandHandler с реальными командами и хранилищем задач.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@SpringBootTest
public class CommandHandlerIntegrationTest {
    @MockBean
    private TelegramBot telegramBot;

    @Autowired
    private CommandHandler commandHandler;

    /**
     * Тестирует полный сценарий взаимодействия с ботом:
     * старт, добавление задач, просмотр списка, удаление задач.
     */
    @Test
    void exampleConversationFlow() {
        Long chatId = 12345L;
        assertEquals("""
                Здравствуйте! Я бот, который поможет вам управлять задачами.
                    Доступные команды:
                    /add [описание] – добавить задачу
                    /todo – показать список задач
                    /delete [номер] – удалить задачу
                    /help – справка по командам
                """, commandHandler.handle("/start", chatId));

        assertEquals("Задача “Купить хлеб” добавлена", commandHandler.handle("/add Купить хлеб", chatId));
        assertEquals("Задача “Позвонить врачу” добавлена", commandHandler.handle("/add Позвонить врачу", chatId));

        assertEquals("1) Купить хлеб\n2) Позвонить врачу", commandHandler.handle("/todo", chatId));

        assertEquals("Задача “Купить хлеб” удалена", commandHandler.handle("/delete 1", chatId));
        assertEquals("Ошибка: задача 5 не найдена", commandHandler.handle("/delete 5", chatId));
    }
}
