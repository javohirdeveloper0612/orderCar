package com.example.ordercar.mytelegram;

import com.example.ordercar.admin.controller.AdminController;
import com.example.ordercar.config.BotConfig;
import com.example.ordercar.controller.CallbackController;
import com.example.ordercar.controller.MainController;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final MainController mainController;
    private final CallbackController callbackController;
    private final AdminController adminController;

    @Lazy
    public MyTelegramBot(BotConfig botConfig, MainController mainController,
                         CallbackController callbackController, AdminController adminController) {
        this.botConfig = botConfig;
        this.mainController = mainController;
        this.callbackController = callbackController;
        this.adminController = adminController;
    }


    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.getChatId() == 1030035146L) {
                adminController.handle(update);
            } else {
                mainController.handler(message);
            }

        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            if (message.getChatId() == 1030035146L) {
                adminController.handle(update);
            } else {
                callbackController.handler(update);
            }



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

    public void send(SendDocument message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Message send(SendLocation message) {
        try {
           return execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Message send(EditMessageText editMessageText) {
        try {
           return (Message) execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
