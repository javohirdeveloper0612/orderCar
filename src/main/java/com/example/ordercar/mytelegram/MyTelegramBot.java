package com.example.ordercar.mytelegram;

import com.example.ordercar.config.BotConfig;
import com.example.ordercar.controller.MainController;
import com.example.ordercar.util.SendMsg;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final MainController mainController;

    @Lazy
    public MyTelegramBot(BotConfig botConfig, MainController mainController) {
        this.botConfig = botConfig;
        this.mainController = mainController;
    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            mainController.handler(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            switch (update.getCallbackQuery().getData()) {
                case "view_loc" -> send(SendMsg.sendLocation(message.getChatId(), message.getMessageId()));
                case "" -> {
                }
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

    public void send(SendLocation message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
