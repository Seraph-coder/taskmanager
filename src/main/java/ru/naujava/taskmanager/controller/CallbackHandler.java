package ru.naujava.taskmanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.controller.callback.CallbackStrategy;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;
import ru.naujava.taskmanager.state.MessageHandler;
import ru.naujava.taskmanager.state.StateTransition;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Обработчик callback данных от inline-кнопок.
 * <p>
 * Использует Strategy Pattern для обработки различных callback-команд.
 * Это решение соответствует принципу Open-Closed (OCP):
 * - Класс открыт для расширения: можно добавить новую стратегию без изменения этого класса
 * - Класс закрыт для модификации: не нужно изменять switch/case при добавлении новой команды
 * <p>
 * Также соблюдается Single Responsibility Principle (SRP):
 * - Каждая стратегия отвечает за обработку одной конкретной команды
 * - CallbackHandler отвечает только за маршрутизацию к нужной стратегии
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
@Component
public class CallbackHandler implements MessageHandler {
    private final Map<String, CallbackStrategy> strategies;
    private final Logger log = LoggerFactory.getLogger(CallbackHandler.class);

    /**
     * Конструктор обработчика.
     * Spring автоматически инжектит все бины, реализующие {@link CallbackStrategy},
     * и создает Map для быстрого поиска стратегии по имени callback.
     *
     * @param strategyList список всех стратегий обработки callback
     */
    public CallbackHandler(List<CallbackStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        CallbackStrategy::getCallbackName,
                        Function.identity()
                ));
        log.info("Зарегистрировано {} callback стратегий: {}",
                strategies.size(), strategies.keySet());
    }

    /**
     * Обрабатывает callbackData от inline-кнопок, делегируя выполнение соответствующей стратегии.
     */
    @Override
    public StateTransition handle(Long chatId, String callbackData) {
        if (callbackData == null || callbackData.isBlank()) {
            return new StateTransition(BotConstants.MSG_EMPTY_CALLBACK,
                    null, KeyboardType.NONE);
        }

        CallbackStrategy strategy = strategies.get(callbackData);
        if (strategy == null) {
            log.warn("Неизвестный callback: {} от пользователя {}", callbackData, chatId);
            return new StateTransition("Неизвестный callback: " + callbackData,
                    null, KeyboardType.NONE);
        }

        return strategy.handle(chatId);
    }
}
