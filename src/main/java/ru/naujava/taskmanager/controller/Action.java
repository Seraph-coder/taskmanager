package ru.naujava.taskmanager.controller;

import org.slf4j.LoggerFactory;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.BotResponse;
import ru.naujava.taskmanager.bot.KeyboardBuilder;
import ru.naujava.taskmanager.state.StateMachine;

import java.util.List;

/**
 * Действия, которые могут быть выполнены после команды.
 *
 * @author Seraph-coder
 * @since 11.12.2025
 */
public enum Action {
    /**
     * Нет действия.
     */
    NONE {
        @Override
        public void handle(StateMachine stateMachine, KeyboardBuilder keyboardBuilder,
                           List<BotResponse> responses, CommandResponse response, Long chatId) {
            // Нет действий
        }
    },
    /**
     * Отправить меню.
     */
    SEND_MENU {
        @Override
        public void handle(StateMachine stateMachine, KeyboardBuilder keyboardBuilder,
                           List<BotResponse> responses, CommandResponse response, Long chatId) {
            // Уже обработано
        }
    },
    /**
     * Отменить добавление задачи.
     */
    CANCEL_ADD_TASK {
        @Override
        public void handle(StateMachine stateMachine, KeyboardBuilder keyboardBuilder,
                           List<BotResponse> responses, CommandResponse response, Long chatId) {
            try {
                stateMachine.resetState(chatId);
            } catch (Exception e) {
                LoggerFactory.getLogger(Action.class)
                        .warn("Ошибка при отмене добавления задачи для chatId={}: {}",
                                chatId, e.getMessage(), e);
            }
        }
    },
    /**
     * Отменить удаление задачи.
     */
    CANCEL_DELETE_TASK {
        @Override
        public void handle(StateMachine stateMachine, KeyboardBuilder keyboardBuilder,
                           List<BotResponse> responses, CommandResponse response, Long chatId) {
            try {
                stateMachine.resetState(chatId);
            } catch (Exception e) {
                LoggerFactory.getLogger(Action.class)
                        .warn("Ошибка при отмене удаления задачи для chatId={}: {}",
                                chatId, e.getMessage(), e);
            }
        }
    },
    /**
     * Сброс состояния пользователя.
     */
    RESET_STATE {
        @Override
        public void handle(StateMachine stateMachine, KeyboardBuilder keyboardBuilder,
                           List<BotResponse> responses, CommandResponse response, Long chatId) {
            try {
                stateMachine.resetState(chatId);
            } catch (Exception e) {
                LoggerFactory.getLogger(Action.class)
                        .warn("Ошибка при сбросе состояния для chatId={}: {}",
                                chatId, e.getMessage(), e);
            }
        }
    },
    /**
     * Установка нового состояния пользователя.
     */
    SET_STATE {
        @Override
        public void handle(StateMachine stateMachine, KeyboardBuilder keyboardBuilder,
                           List<BotResponse> responses, CommandResponse response, Long chatId) {
            try {
                stateMachine.setState(chatId, response.newState());
            } catch (Exception e) {
                LoggerFactory.getLogger(Action.class)
                        .warn("Ошибка при установке состояния для chatId={}: {}",
                                chatId, e.getMessage(), e);
            }
        }
    },
    /**
     * Отправить кнопку отмены.
     */
    SEND_CANCEL {
        @Override
        public void handle(StateMachine stateMachine, KeyboardBuilder keyboardBuilder,
                           List<BotResponse> responses, CommandResponse response, Long chatId) {
            try {
                responses.add(new BotResponse(chatId, BotConstants.MSG_CHOOSE_ACTION,
                        keyboardBuilder.buildCancelKeyboard(), Action.NONE));
                stateMachine.setState(chatId, response.newState());
            } catch (Exception e) {
                LoggerFactory.getLogger(Action.class)
                        .warn("Ошибка при отправке кнопки отмены для chatId={}: {}",
                                chatId, e.getMessage(), e);
            }
        }
    };

    /**
     * Выполняет действие.
     */
    public abstract void handle(StateMachine stateMachine, KeyboardBuilder keyboardBuilder,
                                List<BotResponse> responses, CommandResponse response, Long chatId);
}