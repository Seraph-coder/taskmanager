package ru.naujava.taskmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.entity.UserStateEnum;
import ru.naujava.taskmanager.repository.UserStateRepository;

import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для управления состояниями пользователей.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Service
@Transactional
public class UserStateService {
    private final UserStateRepository userStateRepository;
    private final UserService userService;

    /**
     * Конструктор сервиса состояния пользователя.
     */
    public UserStateService(UserStateRepository userStateRepository, UserService userService) {
        this.userStateRepository = userStateRepository;
        this.userService = userService;
    }

    /**
     * Создает новое состояние пользователя с указанным telegramId,
     * если оно еще не существует.
     * Возвращает Optional с новым состоянием пользователя,
     * или пустой Optional, если состояние уже существует.
     *
     * @throws IllegalArgumentException если пользователь с таким telegramId не существует
     * @throws NullPointerException     если telegramId равен null
     */
    public Optional<UserState> createUserState(Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не может быть null");
        userService.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователя с таким telegramId не существует: " + telegramId));

        if (userStateRepository.findById(telegramId).isPresent()) {
            return Optional.empty();
        }

        UserState newState = new UserState(telegramId);
        return Optional.of(userStateRepository.save(newState));
    }

    /**
     * Возвращает состояние пользователя по telegramId.
     * Если состояние не найдено, создает состояние для пользователя.
     *
     * @throws NullPointerException если telegramId равен null
     */
    @Transactional(readOnly = true)
    public UserStateEnum getUserState(Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не может быть null");
        if (userStateRepository.findById(telegramId).isPresent()) {
            return userStateRepository.findById(telegramId).get().getState();
        } else {
            createUserState(telegramId);
            return UserStateEnum.DEFAULT;
        }
    }

    /**
     * Изменяет состояние пользователя с указанным telegramId.
     * Возвращает Optional с обновленным состоянием пользователя.
     *
     * @throws IllegalArgumentException если пользователь с таким telegramId не существует
     * @throws IllegalArgumentException если состояние пользователя с таким telegramId не существует
     */
    public Optional<UserState> changeUserState(Long telegramId, UserStateEnum newState) {
        Objects.requireNonNull(telegramId, "telegramId не может быть null");
        Objects.requireNonNull(newState, "newState не может быть null");
        userService.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователя с таким telegramId не существует: " + telegramId
                ));

        if (userStateRepository.findById(telegramId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Состояния пользователя с таким telegramId не существует: " + telegramId);
        }

        return userStateRepository.findById(telegramId)
                .map(userState -> {
                    userState.setState(newState);
                    return userStateRepository.save(userState);
                });
    }

    /**
     * Сбрасывает состояние пользователя с указанным telegramId к состоянию по умолчанию.
     * Возвращает true, если сброс выполнен успешно, иначе false.
     *
     * @throws IllegalArgumentException если пользователь с таким telegramId не существует
     */
    public boolean resetUserState(Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не может быть null");
        userService.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователя с таким telegramId не существует: " + telegramId
                ));

        if (userStateRepository.findById(telegramId).isEmpty()) {
            UserState s = new UserState(telegramId);
            s.setState(UserStateEnum.DEFAULT);
            userStateRepository.save(s);
            return true;
        }

        return userStateRepository.findById(telegramId)
                .map(userState -> {
                    userState.setState(UserStateEnum.DEFAULT);
                    userStateRepository.save(userState);
                    return true;
                }).orElse(false);
    }
}
