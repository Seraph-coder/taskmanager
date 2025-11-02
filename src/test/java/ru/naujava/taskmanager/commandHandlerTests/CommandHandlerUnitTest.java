package ru.naujava.taskmanager.commandHandlerTests;

import org.junit.jupiter.api.Test;
import ru.naujava.taskmanager.controller.CommandHandler;
import ru.naujava.taskmanager.controller.CommandRegistry;
import ru.naujava.taskmanager.controller.commands.BotCommand;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для CommandHandler.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
public class CommandHandlerUnitTest {
    /**
     * Тест обработки команды /start.
     */
    @Test
    void HandleStartCommand() {
        CommandRegistry registry = mock(CommandRegistry.class);
        BotCommand startCmd = mock(BotCommand.class);

        when(registry.find("/start")).thenReturn(Optional.of(startCmd));
        when(startCmd.execute("", 1L)).thenReturn("""
                Здравствуйте! Я бот, который поможет вам управлять задачами.
                    Доступные команды:
                    /add [описание] – добавить задачу
                    /todo – показать список задач
                    /delete [номер] – удалить задачу
                    /help – справка по командам
                """);

        CommandHandler handler = new CommandHandler(registry);
        String res = handler.handle("/start", 1L);

        assertTrue(res.startsWith("""
                Здравствуйте! Я бот, который поможет вам управлять задачами.
                    Доступные команды:
                    /add [описание] – добавить задачу
                    /todo – показать список задач
                    /delete [номер] – удалить задачу
                    /help – справка по командам
                """));
        verify(startCmd).execute("", 1L);
    }

    /**
     * Тест обработки команды /add.
     */
    @Test
    void handleAddCommand() {
        CommandRegistry registry = mock(CommandRegistry.class);
        BotCommand addCommand = mock(BotCommand.class);

        when(registry.find("/add")).thenReturn(Optional.of(addCommand));
        when(addCommand.execute("Купить хлеб", 123L)).thenReturn("Задача добавлена");

        CommandHandler handler = new CommandHandler(registry);
        String res = handler.handle("/add Купить хлеб", 123L);

        assertEquals("Задача добавлена", res);
        verify(addCommand, times(1)).execute("Купить хлеб", 123L);
    }

    /**
     * Тест обработки команды /delete.
     */
    @Test
    void handleDeleteCommand() {
        CommandRegistry registry = mock(CommandRegistry.class);
        BotCommand deleteCmd = mock(BotCommand.class);
        when(registry.find("/delete")).thenReturn(Optional.of(deleteCmd));
        when(deleteCmd.execute("5", 99L)).thenReturn("Задача удалена");
        CommandHandler handler = new CommandHandler(registry);
        String res = handler.handle("/delete 5", 99L);
        assertEquals("Задача удалена", res);
        verify(deleteCmd).execute("5", 99L);
    }

    /**
     * Тест обработки команды /todo.
     */
    @Test
    void handleTodoCommand() {
        CommandRegistry registry = mock(CommandRegistry.class);
        BotCommand todoCmd = mock(BotCommand.class);

        when(registry.find("/todo")).thenReturn(Optional.of(todoCmd));
        when(todoCmd.execute("", 42L)).thenReturn("Список задач пуст");

        CommandHandler handler = new CommandHandler(registry);
        String res = handler.handle("/todo", 42L);

        assertEquals("Список задач пуст", res);
        verify(todoCmd).execute("", 42L);
    }

    /**
     * Тест обработки команды /help.
     */
    @Test
    void handleHelpCommand() {
        CommandRegistry registry = mock(CommandRegistry.class);
        BotCommand helpCmd = mock(BotCommand.class);

        when(registry.find("/help")).thenReturn(Optional.of(helpCmd));
        when(helpCmd.execute("", 7L)).thenReturn("""
                Доступные команды:
                /help - показать эту справку
                /add <описание> - добавить задачу
                /todo - показать список задач
                /delete <taskId> - удалить задачу по id
                """);

        CommandHandler handler = new CommandHandler(registry);
        String res = handler.handle("/help", 7L);

        assertEquals("""
                Доступные команды:
                /help - показать эту справку
                /add <описание> - добавить задачу
                /todo - показать список задач
                /delete <taskId> - удалить задачу по id
                """, res);
        verify(helpCmd).execute("", 7L);
    }

    /**
     * Тест обработки неизвестной команды.
     */
    @Test
    void handleUnknownCommand() {
        CommandRegistry registry = mock(CommandRegistry.class);
        when(registry.find("/unknown")).thenReturn(Optional.empty());

        CommandHandler handler = new CommandHandler(registry);
        String res = handler.handle("/unknown", 1L);

        assertEquals("Неизвестная команда. Введите /help для списка команд", res);
    }
}
