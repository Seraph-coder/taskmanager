package ru.naujava.taskmanager.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.naujava.taskmanager.service.UserService;
import ru.naujava.taskmanager.state.StateMachine;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Тесты для команд бота через {@link StateMachine}.
 *
 * @author Seraph-coder
 * @since 17.11.2025
 */
@SpringBootTest
public class CommandIntegrationTest {
    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private UserService userService;

    /**
     * Проверка команд /add и /todo
     * <br>
     * Ожидаемое поведение: добавление задач и их отображение в списке дел.
     */
    @Test
    public void addAndTodoCommands() {
        Long chatId = 12345L;
        userService.getOrCreateByTelegramId(chatId);

        StateTransition responseAdd = stateMachine.processMessage(chatId, "/add Купить молоко");
        StateTransition anotherResponseAdd = stateMachine.processMessage(chatId, "/add Купить хлеб");
        Assertions.assertNotNull(responseAdd.responseText());
        Assertions.assertNotNull(anotherResponseAdd.responseText());

        StateTransition responseTodo = stateMachine.processMessage(chatId, "/todo");
        Assertions.assertEquals("1) Купить молоко\n" +
                "2) Купить хлеб", responseTodo.responseText());
    }

    /**
     * Проверка повторного вызова /add с тем же описанием задачи
     * <br>
     * Ожидаемое поведение: Сообщение об ошибке дубликата задачи, логирование ошибки.
     */
    @Test
    public void addWithDuplicateDescription() {
        Long chatId = 54321L;
        userService.getOrCreateByTelegramId(chatId);

        stateMachine.processMessage(chatId, "/add Сделать домашнее задание");
        StateTransition responseAddDuplicate = stateMachine
                .processMessage(chatId, "/add Сделать домашнее задание");

        Assertions.assertEquals(
                "Ошибка: Задача с описанием 'Сделать домашнее задание' уже существует",
                responseAddDuplicate.responseText());

        StateTransition responseTodo = stateMachine.processMessage(chatId, "/todo");
        Assertions.assertEquals("1) Сделать домашнее задание", responseTodo.responseText());
    }

    /**
     * Проверка команды /delete
     * <br>
     * Ожидаемое поведение: Удаление задачи по номеру и обновление списка дел.
     */
    @Test
    public void deleteCommand() {
        Long chatId = 67890L;
        userService.getOrCreateByTelegramId(chatId);
        stateMachine.processMessage(chatId, "/add Позвонить маме");
        stateMachine.processMessage(chatId, "/add Заплатить за квартиру");
        StateTransition responseDelete = stateMachine.processMessage(chatId, "/delete 1");
        Assertions.assertEquals("Задача “Позвонить маме” удалена", responseDelete.responseText());
        StateTransition responseTodo = stateMachine.processMessage(chatId, "/todo");
        Assertions.assertEquals("1) Заплатить за квартиру", responseTodo.responseText());
    }

    /**
     * Проверка команды /delete с неверным номером задачи и не с числом
     * <br>
     * Ожидаемое поведение: Сообщение об ошибке при попытке удалить несуществующую задачу.
     */
    @Test
    public void deleteWithInvalidTaskNumber() {
        Long chatId = 98765L;
        userService.getOrCreateByTelegramId(chatId);

        stateMachine.processMessage(chatId, "/add Прочитать книгу");
        StateTransition responseDeleteInvalid = stateMachine.processMessage(chatId, "/delete 2");
        Assertions.assertEquals("Ошибка: задача 2 не найдена", responseDeleteInvalid.responseText());
        StateTransition responseDeleteNegative = stateMachine.processMessage(chatId, "/delete -1");
        Assertions.assertEquals(
                "Ошибка: номер задачи должен быть положительным числом",
                responseDeleteNegative.responseText());
        StateTransition responseDeleteZero = stateMachine.processMessage(chatId, "/delete 0");
        Assertions.assertEquals(
                "Ошибка: номер задачи должен быть положительным числом",
                responseDeleteZero.responseText());
        StateTransition responseTodo = stateMachine.processMessage(chatId, "/todo");
        Assertions.assertEquals(
                "1) Прочитать книгу", responseTodo.responseText());
        StateTransition responseDeleteNonNumber = stateMachine.processMessage(chatId, "/delete abc");
        Assertions.assertEquals("Ошибка: номер задачи должен быть числом",
                responseDeleteNonNumber.responseText());
    }

    /**
     * Проверка команды с неизвестным именем.
     * <br>
     * Ожидаемое поведение: возвращается сообщение об неизвестной команде.
     */
    @Test
    public void unknownCommand() {
        Long chatId = 11111L;
        userService.getOrCreateByTelegramId(chatId);

        StateTransition response = stateMachine.processMessage(chatId, "/unknown");

        Assertions.assertEquals("Неизвестная команда. Введите /help для списка команд",
                response.responseText());
        Assertions.assertTrue(response.shouldSendMenu());
    }

    /**
     * Проверка обработки пустого сообщения.
     * <br>
     * Ожидаемое поведение: возвращается сообщение о пустом сообщении.
     */
    @Test
    public void emptyMessage() {
        Long chatId = 22222L;
        userService.getOrCreateByTelegramId(chatId);

        StateTransition response = stateMachine.processMessage(chatId, "");

        Assertions.assertEquals("Пустое сообщение", response.responseText());
        Assertions.assertFalse(response.shouldSendMenu());
    }
}
