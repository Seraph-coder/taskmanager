package ru.naujava.taskmanager.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.naujava.taskmanager.controller.command.AddTaskCommand;
import ru.naujava.taskmanager.controller.command.DeleteTaskCommand;

/**
 * Тесты для команд бота {@link CommandHandler}.
 *
 * @author Seraph-coder
 * @since 17.11.2025
 */
@SpringBootTest
public class CommandIntegrationTest {
    @Autowired
    private CommandHandler commandHandler;

    /**
     * Проверка команд /add и /todo
     * <br>
     * Ожидаемое поведение: добавление задач и их отображение в списке дел.
     */
    @Test
    public void addAndTodoCommands() {
        Long chatId = 12345L;

        String responseAdd = commandHandler.handle("/add Купить молоко", chatId);
        String anotherResponseAdd = commandHandler.handle("/add Купить хлеб", chatId);
        Assertions.assertNotNull(responseAdd);
        Assertions.assertNotNull(anotherResponseAdd);

        String responseTodo = commandHandler.handle("/todo", chatId);
        Assertions.assertEquals("1) Купить молоко\n" +
                "2) Купить хлеб", responseTodo);
    }

    /**
     * Проверка повторного вызова /add с тем же описанием задачи
     * <br>
     * Ожидаемое поведение: Сообщение об ошибке дубликата задачи, логирование ошибки.
     */
    @Test
    public void addWithDuplicateDescription() {
        Long chatId = 54321L;

        Logger logger = (Logger) LoggerFactory.getLogger(AddTaskCommand.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        commandHandler.handle("/add Сделать домашнее задание", chatId);
        String responseAddDuplicate = commandHandler
                .handle("/add Сделать домашнее задание", chatId);

        Assertions.assertEquals(
                "Ошибка: Задача с описанием 'Сделать домашнее задание' уже существует",
                responseAddDuplicate);

        String responseTodo = commandHandler.handle("/todo", chatId);
        Assertions.assertEquals("1) Сделать домашнее задание", responseTodo);

        boolean hasErrorLog = listAppender.list.stream()
                .allMatch(ev ->
                        ev.getLevel().toString().equals("ERROR")
                                &&
                                ev.getFormattedMessage().equals(
                                        "Не удалось добавить задачу. Причина: Задача с описанием " +
                                                "'Сделать домашнее задание' уже существует"
                                )
                );
        Assertions.assertTrue(hasErrorLog);
    }

    /**
     * Проверка команды /delete
     * <br>
     * Ожидаемое поведение: Удаление задачи по номеру и обновление списка дел.
     */
    @Test
    public void deleteCommand() {
        Long chatId = 67890L;
        commandHandler.handle("/add Позвонить маме", chatId);
        commandHandler.handle("/add Заплатить за квартиру", chatId);
        String responseDelete = commandHandler.handle("/delete 1", chatId);
        Assertions.assertEquals("Задача “Позвонить маме” удалена", responseDelete);
        String responseTodo = commandHandler.handle("/todo", chatId);
        Assertions.assertEquals("1) Заплатить за квартиру", responseTodo);
    }

    /**
     * Проверка команды /delete с неверным номером задачи и не с числом
     * <br>
     * Ожидаемое поведение: Сообщение об ошибке при попытке удалить несуществующую задачу,
     * логирование ошибки.
     */
    @Test
    public void deleteWithInvalidTaskNumber() {
        Long chatId = 98765L;

        Logger logger = (Logger) LoggerFactory.getLogger(DeleteTaskCommand.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        commandHandler.handle("/add Прочитать книгу", chatId);
        String responseDeleteInvalid = commandHandler.handle("/delete 2", chatId);
        Assertions.assertEquals("Ошибка: задача 2 не найдена", responseDeleteInvalid);
        String responseDeleteNegative = commandHandler.handle("/delete -1", chatId);
        Assertions.assertEquals(
                "Ошибка: номер задачи должен быть положительным числом", responseDeleteNegative);
        String responseDeleteZero = commandHandler.handle("/delete 0", chatId);
        Assertions.assertEquals(
                "Ошибка: номер задачи должен быть положительным числом", responseDeleteZero);
        Assertions.assertEquals(
                "1) Прочитать книгу", commandHandler.handle("/todo", chatId));

        String responseDeleteNonNumber = commandHandler.handle("/delete abc", chatId);
        Assertions.assertEquals("Ошибка: номер задачи должен быть числом", responseDeleteNonNumber);

        boolean hasErrorLog = listAppender.list.stream()
                .allMatch(ev ->
                        ev.getLevel().toString().equals("ERROR")
                                &&
                                ev.getFormattedMessage().equals(
                                        "Не удалось удалить задачу. Причина: номер задачи должен быть " +
                                                "числом"
                                )
                );
        Assertions.assertTrue(hasErrorLog);
    }
}
