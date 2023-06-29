package com.example.ordercar.controller;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.service.DriverService;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.TelegramUsers;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

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

            var transportStep = saveUser(message.getChatId());
            if (message.hasText()) {
                String text = message.getText();

                switch (text) {
                    case "/start" -> {
                        driverService.menu(message);
                    }

                    case ButtonName.activeOrder -> {
                        driverService.sendOrder(message);
                    }

                    case ButtonName.notActiveOrder -> {
                        driverService.orderList(message);
                    }
                }
            }
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
