package com.example.ordercar.mytelegram;

import com.example.ordercar.admin.controller.AdminMainController;
import com.example.ordercar.config.BotConfig;
import com.example.ordercar.controller.CallbackController;
import com.example.ordercar.controller.MainController;
import com.example.ordercar.util.Step;
import com.example.ordercar.util.TelegramUsers;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final com.example.ordercar.controller.MainController mainController;
    private final CallbackController callbackController;
    private final AdminMainController adminMainController;

    List<TelegramUsers> usersList = new ArrayList<>();

    @Lazy
    public MyTelegramBot(BotConfig botConfig,
                         MainController mainController,
                         CallbackController callbackController, AdminMainController adminMainController) {

        this.botConfig = botConfig;
        this.mainController = mainController;
        this.callbackController = callbackController;
        this.adminMainController = adminMainController;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Long userId = update.getMessage().getFrom().getId();
        TelegramUsers telegramUsers = saveUser(userId);

        Message message = update.getMessage();

        if (telegramUsers.getStep() == null || telegramUsers.getStep().equals(Step.MAIN)) {

            if (userId == 5530157790L) {
                adminMainController.adminhandle(message);
                return;
            }
        }

        if (update.hasMessage()) {
            mainController.handler(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            callbackController.handler(update);

        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(SendLocation message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public TelegramUsers saveUser(Long chatId) {

        for (TelegramUsers users : usersList) {
            if (users.getChatId().equals(chatId)) {
                return users;
            }
        }

        TelegramUsers users = new TelegramUsers();
        users.setChatId(chatId);
        usersList.add(users);

        return users;
    }
}
