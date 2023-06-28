package com.example.ordercar.controller;


import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.SendMsg;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
public class CallbackController {

    private final MyTelegramBot myTelegramBot;

    @Lazy
    public CallbackController(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void handler(Update update) {

        Message message = update.getCallbackQuery().getMessage();

        switch (update.getCallbackQuery().getData()) {
            case "view_loc" -> myTelegramBot.send(SendMsg.sendLocation(message.getChatId(), message.getMessageId()));
            case "" -> {
            }
        }
    }
}
