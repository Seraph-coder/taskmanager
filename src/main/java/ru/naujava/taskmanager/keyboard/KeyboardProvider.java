package ru.naujava.taskmanager.keyboard;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.keyboard.KeyboardService;
import ru.naujava.taskmanager.keyboard.model.Keyboard;
import ru.naujava.taskmanager.keyboard.model.KeyboardType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Провайдер клавиатур, который предоставляет клавиатуру по типу.
 * <p>
 * Использует паттерн Strategy через Map для избежания switch/case,
 * что соответствует принципу Open-Closed (OCP).
 * </p>
 *
 * @author Seraph-coder
 * @since 02.01.2026
 */
@Component
public class KeyboardProvider {
    private final Map<KeyboardType, Supplier<Keyboard>> keyboardSuppliers;

    /**
     * Конструктор инициализирует Map с поставщиками клавиатур.
     *
     * @param keyboardService сервис для создания клавиатур
     */
    public KeyboardProvider(KeyboardService keyboardService) {
        this.keyboardSuppliers = new EnumMap<>(KeyboardType.class);
        keyboardSuppliers.put(KeyboardType.MAIN_MENU, keyboardService::buildMainMenu);
        keyboardSuppliers.put(KeyboardType.CANCEL, keyboardService::buildCancelKeyboard);
        keyboardSuppliers.put(KeyboardType.NONE, () -> null);
    }

    /**
     * Возвращает клавиатуру для указанного типа.
     *
     * @param type тип клавиатуры
     * @return клавиатура или null для типа NONE
     */
    public Keyboard getKeyboard(KeyboardType type) {
        Supplier<Keyboard> supplier = keyboardSuppliers.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Неизвестный тип клавиатуры: " + type);
        }
        return supplier.get();
    }
}
