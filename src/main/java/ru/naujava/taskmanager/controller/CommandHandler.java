package ru.naujava.taskmanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naujava.taskmanager.bot.BotConstants;
import ru.naujava.taskmanager.bot.dto.KeyboardType;
import ru.naujava.taskmanager.controller.command.BotCommand;
import ru.naujava.taskmanager.entity.UserState;
import ru.naujava.taskmanager.state.MessageHandler;
import ru.naujava.taskmanager.state.StateHandler;
import ru.naujava.taskmanager.state.StateTransition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Обработчик команд для состояния DEFAULT.
 * Парсит команды и выполняет соответствующие действия.
 *
 * @author Seraph-coder
 * @since 16.12.2025
 */
@Component
public class CommandHandler implements MessageHandler, StateHandler {
    private final Map<String, BotCommand> commands;
    private final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    /**
     * Конструктор обработчика команд.
     */
    public CommandHandler(List<BotCommand> commandsList) {
        this.commands = commandsList.stream()
                .collect(Collectors.toMap(
                        c -> c.getCommandName().toLowerCase(),
                        Function.identity()
                ));
    }

    @Override
    public UserState getState() {
        return UserState.DEFAULT;
    }

    @Override
    public StateTransition handle(Long chatId, String text) {
        if (text == null || text.isBlank()) {
            return new StateTransition("Пустое сообщение", null);
        }

        String trimmed = text.trim();
        String cmd = trimmed.split("\\s+")[0].toLowerCase();

        if ("/cancel".equalsIgnoreCase(cmd)) {
            return new StateTransition(BotConstants.MSG_ACTION_CANCELLED,
                    UserState.DEFAULT, KeyboardType.MAIN_MENU, Action.NONE);
        }

        CommandResponse response = Optional.ofNullable(commands.get(cmd))
                .map(c -> c.execute("", chatId))
                .orElseGet(() -> {
                    log.warn("Неизвестная команда '{}' от пользователя {}", cmd, chatId);
                    return new CommandResponse(
                            BotConstants.MSG_UNKNOWN_COMMAND,
                            Action.NONE, null, KeyboardType.MAIN_MENU);
                });

        return new StateTransition(response.text(), response.newState(), response.keyboardType(),
                response.action());
    }
}
