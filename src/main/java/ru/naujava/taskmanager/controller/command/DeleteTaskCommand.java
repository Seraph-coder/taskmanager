package ru.naujava.taskmanager.controller.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.service.TaskService;

import java.util.List;

/**
 * Команда для удаления задачи по её номеру в списке.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class DeleteTaskCommand implements BotCommand {
    private final Logger log = LoggerFactory.getLogger(DeleteTaskCommand.class);
    private final TaskService taskService;

    /**
     * Конструктор удаления задачи.
     */
    public DeleteTaskCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/delete";
    }

    @Override
    public CommandResponse execute(String taskId, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(
                    BotConstants.MSG_UNKNOWN_USER,
                    Action.NONE, null, true);
        }
        if (taskId == null || taskId.isBlank()) {
            return new CommandResponse(
                    "Использование: /delete <taskId>", Action.NONE,
                    null, true);
        }

        int taskIndex;

        try {
            taskIndex = Integer.parseInt(taskId.trim());
        } catch (NumberFormatException e) {
            log.warn("Не удалось удалить задачу. Причина: номер задачи должен быть числом", e);
            return new CommandResponse(
                    "Ошибка: номер задачи должен быть числом",
                    Action.NONE, null, true);
        }

        if (taskIndex <= 0) {
            return new CommandResponse(
                    "Ошибка: номер задачи должен быть положительным числом",
                    Action.NONE, null, true);
        }

        List<Task> tasks = taskService.findAllTasksByTelegramId(chatId);
        if (tasks == null || tasks.isEmpty()) {
            return new CommandResponse(
                    BotConstants.MSG_TASKS_EMPTY, Action.NONE, null);
        }
        if (taskIndex > tasks.size()) {
            return new CommandResponse(
                    "Ошибка: задача " + taskIndex + " не найдена",
                    Action.NONE, null, true);
        }

        Task toDelete = tasks.get(taskIndex - 1);
        Task deleted = taskService.deleteTaskByIdAndTelegramId(toDelete.getId(), chatId);

        return new CommandResponse(
                "Задача “" + deleted.getDescription() + "” удалена",
                Action.NONE, null, true);
    }
}
