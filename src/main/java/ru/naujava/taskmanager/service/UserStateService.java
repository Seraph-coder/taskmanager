package ru.naujava.taskmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.entity.UserStateEnum;
import ru.naujava.taskmanager.repository.UserStateRepository;

import java.util.Objects;

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
     * Возвращает состояние пользователя по telegramId, если оно существует.
     * Если состояние не найдено, создает состояние для пользователя.
     *
     * @throws NullPointerException если telegramId равен null
     * @throws IllegalArgumentException если пользователь с таким telegramId не существует
     */
    @Transactional(readOnly = true)
    public UserStateEnum getOrCreateUserState(Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не может быть null");
        if (userService.findByTelegramId(telegramId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Пользователя с таким telegramId не существует: " + telegramId);
        }
        if (userStateRepository.findById(telegramId).isPresent()) {
            return userStateRepository.findById(telegramId).get().getState();
        }
        UserState newUserState = new UserState();
        newUserState.setTelegramId(telegramId);
        userStateRepository.save(newUserState);
        return newUserState.getState();
    }

    /**
     * Изменяет состояние пользователя с указанным telegramId.
     * Если состояние не найдено, создает его.
     *
     * @throws IllegalArgumentException если пользователь с таким telegramId не существует
     * @throws NullPointerException если telegramId или newState равны null
     */
    public void changeUserState(Long telegramId, UserStateEnum newState) {
        Objects.requireNonNull(telegramId, "telegramId не может быть null");
        Objects.requireNonNull(newState, "newState не может быть null");
        userService.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователя с таким telegramId не существует: " + telegramId));

        if (userStateRepository.findById(telegramId).isEmpty()) {
            getOrCreateUserState(telegramId);
        }

        userStateRepository.findById(telegramId)
                .ifPresent(userState -> {
                    userState.setState(newState);
                    userStateRepository.save(userState);
                });
    }
}