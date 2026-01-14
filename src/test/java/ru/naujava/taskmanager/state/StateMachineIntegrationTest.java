package ru.naujava.taskmanager.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.CallbackHandler;
import ru.naujava.taskmanager.controller.CommandHandler;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.service.TaskService;

import java.util.List;

/**
 * Интеграционные тесты для {@link StateMachine}.
 * Косвенно тестируемые классы: {@link CommandHandler}, {@link CallbackHandler},
 * {@link AwaitingTaskDescriptionHandler}, {@link AwaitingTaskIdForDeletionHandler}.
 */
@SpringBootTest
@Transactional
class StateMachineIntegrationTest {

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private TaskService taskService;

    private final long chatId = 100500L;

    /**
     * Проверяет успешное добавление задачи через команду /add.
     */
    @Test
    void successfulAddTaskViaCommand() {
        stateMachine.processMessage(chatId, "/add");
        StateTransition result = stateMachine.processMessage(chatId, "Купить молоко и хлеб");

        assertResponseContains(result, "Задача 'Купить молоко и хлеб' добавлена");

        List<Task> tasks = taskService.getUncompletedTasks(chatId);
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals("Купить молоко и хлеб", tasks.getFirst().getDescription());
    }

    /**
     * Проверяет успешное удаление задачи через команду /delete.
     */
    @Test
    void successfulDeleteTaskViaCommand() {
        taskService.createTask("Мусор вынести", chatId);

        stateMachine.processMessage(chatId, "/delete");
        StateTransition result = stateMachine.processMessage(chatId, "1");

        assertResponseContains(result, "удален", "удалена");

        Assertions.assertTrue(taskService.getUncompletedTasks(chatId).isEmpty());
    }

    /**
     * Проверяет отмену действия через команду /cancel.
     */
    @Test
    void cancelShouldResetStateAndReturnToMainMenu() {
        stateMachine.processMessage(chatId, "/add");

        StateTransition result = stateMachine.processMessage(chatId, "/cancel");

        assertResponseContains(result, "отмен", "Действие отменено");

        List<Task> tasks = taskService.getUncompletedTasks(chatId);
        Assertions.assertTrue(tasks.isEmpty(), "Не должно создаваться задач при отмене");
    }

    /**
     * Проверяет успешное завершение задачи через callback "DONE".
     */
    @Test
    void markTaskAsDoneHappyPath() {
        taskService.createTask("Сходить в зал", chatId);
        taskService.createTask("Позвонить маме", chatId);

        stateMachine.processMessage(chatId, BotConstants.CALLBACK_DONE);
        StateTransition result = stateMachine.processMessage(chatId, "1");

        assertResponseContains(result, "отмечен", "выполнен", "Сходить в зал");

        List<Task> active = taskService.getUncompletedTasks(chatId);
        List<Task> completed = taskService.getCompletedTasks(chatId);

        Assertions.assertEquals(1, active.size());
        Assertions.assertEquals(1, completed.size());
        Assertions.assertEquals("Сходить в зал", completed.getFirst().getDescription());
    }

    /**
     * Проверяет обработку неверного номера задачи при удалении.
     */
    @Test
    void invalidTaskNumberDuringDeletionShouldAskAgain() {
        taskService.createTask("Задача", chatId);

        stateMachine.processMessage(chatId, "/delete");

        StateTransition result1 = stateMachine.processMessage(chatId, "999");
        StateTransition result2 = stateMachine.processMessage(chatId, "рыба");

        assertResponseContains(result1, "не найдена", "Попробуйте еще раз");
        assertResponseContains(result2, "Неверный номер", "Попробуйте еще раз");

        Assertions.assertEquals(1, taskService.getUncompletedTasks(chatId).size());
    }

    /**
     * Проверяет обработку неизвестной команды.
     */
    private void assertResponseContains(StateTransition transition, String... substrings) {
        String text = transition.responseText();
        for (String substring : substrings) {
            Assertions.assertTrue(text.toLowerCase().contains(substring.toLowerCase()),
                    "Ожидалось содержание '" + substring + "' в ответе, но получили:\n" + text);
        }
    }
}
