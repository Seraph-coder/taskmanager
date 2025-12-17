package ru.naujava.taskmanager.state;

import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.UserState;

import java.util.Map;
import java.util.Set;

/**
 * Конфигурация допустимых переходов состояний.
 *
 * @author Seraph-coder
 * @since 17.12.2025
 */
@Component
public class TransitionConfig {

    /**
     * Возвращает карту допустимых переходов состояний.
     */
    public Map<UserState, Set<UserState>> getAllowedTransitions() {
        return Map.of(
                UserState.DEFAULT,
                Set.of(UserState.AWAITING_TASK_DESCRIPTION, UserState.AWAITING_TASK_ID_FOR_DELETION),
                UserState.AWAITING_TASK_DESCRIPTION,
                Set.of(UserState.DEFAULT),
                UserState.AWAITING_TASK_ID_FOR_DELETION,
                Set.of(UserState.DEFAULT)
        );
    }
}
