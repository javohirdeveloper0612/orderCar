package com.example.ordercar.controller;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.service.DriverService;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.SendMsg;
import com.example.ordercar.util.TelegramUsers;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputLocationMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputMessageContent;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Controller
public class DriverController {
    private final MyTelegramBot myTelegramBot;

    private final DriverService driverService;

    private List<TelegramUsers> usersList = new LinkedList<>();

    @Lazy
    public DriverController(MyTelegramBot myTelegramBot, DriverService driverService) {
        this.myTelegramBot = myTelegramBot;
        this.driverService = driverService;
    }

    public void handler(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();

                if (text.equals("Asosiy Menyu !") || text.equals("/start")) {
                    driverService.menu(message);
                    return;
                }

                switch (text) {

                    case ButtonName.activeOrder -> {
                        driverService.activeOrder(message);
                        return;
                    }

                    case ButtonName.notActiveOrder -> {
                        driverService.orderList(message);
                        return;
                    }

                    case ButtonName.acceptOrder -> {
                        driverService.acceptOrderList(message);
                        return;
                    }
                }
                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "*Siz noto'g'ri buyruq kiritdingiz*"));
                return;
            }
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "*Siz noto'g'ri buyruq kiritdingiz*"));
        }
    }

    public TelegramUsers saveUser(Long chatId) {
        for (TelegramUsers users : usersList) {
            if (users.getChatId().equals(chatId)) {
                return users;
            }
        }
        var users = new TelegramUsers();
        users.setChatId(chatId);
        usersList.add(users);
        return users;
    }
}
