package ru.naujava.taskmanager.controller.callback;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.service.TaskService;
import ru.naujava.taskmanager.state.StateTransition;

/**
 * Стратегия для обработки callback списка выполненных задач.
 *
 * @author Seraph-coder
 * @since 02.01.2026
 */
@Component
public class ShowDoneCallbackStrategy implements CallbackStrategy {
    private final TaskService taskService;

    /**
     * Конструктор стратегии списка выполненных задач.
     */
    public ShowDoneCallbackStrategy(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public StateTransition handle(Long chatId) {
        String text = taskService.formatCompletedTaskAsString(chatId);
        return new StateTransition(text, null, KeyboardType.MAIN_MENU);
    }

    @Override
    public String getCallbackName() {
        return BotConstants.CALLBACK_SHOWDONE;
    }
}
