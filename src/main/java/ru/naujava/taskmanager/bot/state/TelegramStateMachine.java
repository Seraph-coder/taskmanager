package ru.naujava.taskmanager.bot.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.entity.UserStateEnum;
import ru.naujava.taskmanager.service.UserStateService;

/**
 * Реализация стейтмашины для Telegram бота.
 * Использует UserStateService для управления состояниями пользователей.
 *
 * @author Seraph-coder
 * @since 25.11.2025
 */
@Component
public class TelegramStateMachine implements StateMachine {
    private final UserStateService userStateService;

    /**
     * Конструктор стейтмашины.
     */
    public TelegramStateMachine(UserStateService userStateService) {
        this.userStateService = userStateService;
    }

    @Override
    public UserStateEnum getState(Long chatId) {
        try {
            return userStateService.getUserState(chatId);
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(TelegramStateMachine.class);
            log.warn("Не удалось получить состояние для chatId={}", chatId, e);
            return UserStateEnum.DEFAULT;
        }
    }

    @Override
    public void setState(Long chatId, UserStateEnum state) {
        try {
            userStateService.changeUserState(chatId, state);
        } catch (Exception e) {
            try {
                userStateService.createUserState(chatId);
                userStateService.changeUserState(chatId, state);
            } catch (Exception ex) {
                Logger log = LoggerFactory.getLogger(TelegramStateMachine.class);
                log.warn("Не удалось установить состояние {} для chatId={}", state, chatId, ex);
            }
        }
    }

    @Override
    public void reset(Long chatId) {
        try {
            userStateService.resetUserState(chatId);
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(TelegramStateMachine.class);
            log.warn("Не удалось сбросить состояние для chatId={}", chatId, e);
        }
    }
}
