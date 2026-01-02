package ru.naujava.taskmanager.controller.command;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.CommandResponse;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;


/**
 * Команда для удаления задачи по её номеру в списке.
 *
 * @author Seraph-coder
 * @since 02.11.2025
 */
@Component
public class DeleteTaskCommand implements BotCommand {

    /**
     * Конструктор удаления задачи.
     */
    public DeleteTaskCommand() {
    }

    @Override
    public String getCommandName() {
        return "/delete";
    }

    @Override
    public CommandResponse execute(String taskId, Long chatId) {
        if (chatId == null) {
            return new CommandResponse(
                    BotConstants.MSG_UNKNOWN_USER, null, KeyboardType.NONE);
        }
        return new CommandResponse(BotConstants.MSG_ENTER_TASK_NUMBER_DELETE,
                UserState.AWAITING_TASK_ID_FOR_DELETION, KeyboardType.CANCEL);
    }
}
