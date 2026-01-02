package ru.naujava.taskmanager.controller.callback;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Стратегия для обработки callback добавления задачи.
 *
 * @author Seraph-coder
 * @since 02.01.2026
 */
@Component
public class AddTaskCallbackStrategy implements CallbackStrategy {
    @Override
    public StateTransition handle(Long chatId) {
        return new StateTransition(
                BotConstants.MSG_ENTER_TASK_DESCRIPTION,
                UserState.AWAITING_TASK_DESCRIPTION,
                KeyboardType.CANCEL
        );
    }

    @Override
    public String getCallbackName() {
        return BotConstants.CALLBACK_ADD;
    }
}
