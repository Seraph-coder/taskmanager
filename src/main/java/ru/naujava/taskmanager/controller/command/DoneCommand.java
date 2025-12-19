package ru.naujava.taskmanager.controller.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.Action;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.entity.Task;
import ru.naujava.taskmanager.service.TaskService;

/**
 * Команда для отметки задачи как выполненной по её номеру в списке.
 *
 * @author Seraph-coder
 * @since 18.12.2025
 */
@Component
public class DoneCommand implements BotCommand {
    private final Logger log = LoggerFactory.getLogger(DoneCommand.class);
    private final TaskService taskService;

    /**
     * Конструктор команды отметки задачи как выполненной.
     */
    public DoneCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public String getCommandName() {
        return "/done";
    }

    @Override
    public CommandResponse execute(String taskId, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(
                    BotConstants.MSG_UNKNOWN_USER,
                    Action.NONE, null, true);
        }

        int taskIndex;
        try {
            if (taskId == null || taskId.isBlank()) {
                throw new IllegalArgumentException("Номер задачи не указан");
            }
            taskIndex = Integer.parseInt(taskId.trim());
            if (taskIndex <= 0) {
                throw new IllegalArgumentException("Номер задачи должен быть положительным числом");
            }
        } catch (NumberFormatException e) {
            log.warn("Не удалось отметить задачу как выполненную. Причина: номер задачи должен быть числом");
            return new CommandResponse("Ошибка: номер задачи должен быть числом", Action.NONE, null, true);
        } catch (IllegalArgumentException e) {
            log.warn("Не удалось отметить задачу как выполненную. Причина: {}", e.getMessage());
            return new CommandResponse("Ошибка: " + e.getMessage(), Action.NONE, null, true);
        }

        try {
            Task marked = taskService.markTaskCompletedByIndexAndTelegramId(taskIndex, chatId);
            return new CommandResponse(
                    "Задача “" + marked.getDescription() + "” отмечена как выполненная",
                    Action.NONE, null, true);
        } catch (IllegalArgumentException e) {
            log.warn("Не удалось отметить задачу как выполненную. Причина: {}", e.getMessage());
            return new CommandResponse(e.getMessage(), Action.NONE, null, true);
        }
    }
}
