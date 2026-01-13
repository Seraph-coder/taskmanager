package ru.naujava.taskmanager.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.controller.callback.CallbackProcessor;
import ru.naujava.taskmanager.controller.callback.CallbackStrategy;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.state.StateTransition;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Обработчик callback данных от inline-кнопок.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
@Component
public class CallbackHandler implements CallbackProcessor {
    private final Map<String, CallbackStrategy> strategies;
    private final Logger log = LoggerFactory.getLogger(CallbackHandler.class);

    /**
     * Конструктор обработчика.
     */
    public CallbackHandler(List<CallbackStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        CallbackStrategy::getCallbackName,
                        Function.identity()
                ));
        log.debug("Зарегистрировано {} callback стратегий: {}",
                strategies.size(), strategies.keySet());
    }

    @Override
    public StateTransition processCallback(String callbackData, Long chatId) {
        if (callbackData == null || callbackData.isBlank()) {
            return new StateTransition("Пустой callback", null, KeyboardType.NONE);
        }

        CallbackStrategy strategy = strategies.get(callbackData);
        if (strategy == null) {
            log.warn("Неизвестный callback: {} от пользователя {}", callbackData, chatId);
            return new StateTransition("Неизвестный callback: " + callbackData,
                    null, KeyboardType.NONE);
        }

        return strategy.handle(chatId);
    }

    @Override
    public boolean isCallback(String callbackData) {
        return callbackData != null && strategies.containsKey(callbackData);
    }
}
