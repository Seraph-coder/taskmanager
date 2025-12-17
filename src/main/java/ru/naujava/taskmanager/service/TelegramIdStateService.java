package ru.naujava.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.naujava.taskmanager.entity.TelegramIdState;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.repository.TelegramIdStateRepository;

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
public class TelegramIdStateService {
    private final Logger log = LoggerFactory.getLogger(TelegramIdStateService.class);
    private final TelegramIdStateRepository telegramIdStateRepository;
    private final UserService userService;

    /**
     * Конструктор сервиса состояния пользователя.
     */
    public TelegramIdStateService(TelegramIdStateRepository
                                          telegramIdStateRepository, UserService userService) {
        this.telegramIdStateRepository = telegramIdStateRepository;
        this.userService = userService;
    }

    /**
     * Возвращает состояние пользователя по telegramId, если оно существует.
     * Если состояние не найдено, создает состояние для пользователя.
     *
     * @throws NullPointerException     если telegramId равен null
     * @throws IllegalArgumentException если пользователь с таким telegramId не существует
     */
    public UserState getOrCreateUserState(Long telegramId) {
        return getOrCreateUserStateEntity(telegramId).getState();
    }

    /**
     * Возвращает сущность состояния пользователя по telegramId.
     * Если состояние не найдено, создает его.
     */
    private TelegramIdState getOrCreateUserStateEntity(Long telegramId) {
        Objects.requireNonNull(telegramId, "telegramId не может быть null");
        if (userService.findByTelegramId(telegramId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Пользователя с таким telegramId не существует: " + telegramId);
        }
        Optional<TelegramIdState> userStateOpt = telegramIdStateRepository.findById(telegramId);
        if (userStateOpt.isPresent()) {
            return userStateOpt.get();
        }
        TelegramIdState newUserState = new TelegramIdState();
        newUserState.setTelegramId(telegramId);
        telegramIdStateRepository.save(newUserState);
        log.info("Создано новое состояние для пользователя с telegramId: {}", telegramId);
        return newUserState;
    }

    /**
     * Изменяет состояние пользователя с указанным telegramId.
     * Если состояние не найдено, создает его.
     *
     * @throws IllegalArgumentException если telegramId или newState равны null, или пользователь не существует
     */
    public void changeUserState(Long telegramId, UserState newState) {
        Objects.requireNonNull(telegramId, "telegramId не может быть null");
        Objects.requireNonNull(newState, "newState не может быть null");
        TelegramIdState userState = getOrCreateUserStateEntity(telegramId);
        userState.setState(newState);
        telegramIdStateRepository.save(userState);
    }
}
