package ru.naujava.taskmanager.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.naujava.taskmanager.entity.UserIdState;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.repository.UserIdStateRepository;

import java.util.Objects;

/**
 * Сервис для управления состояниями пользователей.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Service
public class UserIdStateService {
    private final Logger log = LoggerFactory.getLogger(UserIdStateService.class);
    private final UserIdStateRepository userIdStateRepository;

    /**
     * Конструктор сервиса состояния пользователя.
     */
    public UserIdStateService(UserIdStateRepository userIdStateRepository) {
        this.userIdStateRepository = userIdStateRepository;
    }

    /**
     * Возвращает состояние пользователя по userId.
     * Если состояние не найдено, возвращает null.
     *
     * @throws NullPointerException если userId равен null
     */
    UserState getUserState(Long userId) {
        Objects.requireNonNull(userId, "userId не может быть null");

        return userIdStateRepository.findById(userId)
                .map(UserIdState::getState)
                .orElse(null);
    }

    /**
     * Создает нового пользователя и его состояние.
     *
     * @param userId идентификатор пользователя
     * @return состояние созданного пользователя (DEFAULT)
     */
    UserState createUserState(Long userId) {
        Objects.requireNonNull(userId, "userId не может быть null");

        UserIdState newUserState = new UserIdState();
        newUserState.setUserId(userId);

        userIdStateRepository.save(newUserState);
        log.debug("Создано новое состояние для пользователя с userId: {}", userId);

        return newUserState.getState();
    }

    /**
     * Изменяет состояние пользователя с указанным userId.
     * Если пользователя или его состояния не существует, они будут созданы перед изменением состояния.
     *
     * @param userId   идентификатор пользователя
     * @param newState новое состояние пользователя
     * @throws NullPointerException если userId или newState равны null
     */
    void changeUserState(Long userId, UserState newState) {
        Objects.requireNonNull(userId, "userId не может быть null");
        Objects.requireNonNull(newState, "newState не может быть null");

        UserIdState userState = userIdStateRepository.findById(userId)
                .orElseGet(() -> createUserStateEntity(userId));

        userState.setState(newState);
        userIdStateRepository.save(userState);
        log.debug("Состояние пользователя {} изменено на {}", userId, newState);
    }

    /**
     * Создает сущность состояния для пользователя.
     *
     * @param userId идентификатор пользователя
     * @return созданная сущность UserIdState
     */
    UserIdState createUserStateEntity(Long userId) {
        UserIdState newUserState = new UserIdState();
        newUserState.setUserId(userId);
        userIdStateRepository.save(newUserState);

        log.debug("Создана новая сущность состояния для пользователя с userId: {}", userId);
        return newUserState;
    }
}