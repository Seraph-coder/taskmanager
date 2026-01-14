package ru.naujava.taskmanager.controller.callback;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Стратегия для обработки callback отмены действия.
 *
 * @author Seraph-coder
 * @since 02.01.2026
 */
@Component
public class CancelCallbackStrategy implements CallbackStrategy {

    @Override
    public StateTransition handle(Long chatId) {
        return new StateTransition(
                BotConstants.MSG_ACTION_CANCELLED,
                UserState.DEFAULT,
                KeyboardType.MAIN_MENU
        );
    }

    @Override
    public String getCallbackName() {
        return BotConstants.CALLBACK_CANCEL;
    }
}
