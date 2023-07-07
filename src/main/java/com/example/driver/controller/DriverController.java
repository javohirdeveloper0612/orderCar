package com.example.driver.controller;

import com.example.mytelegram.MyTelegramBot;
import com.example.driver.service.DriverService;
import com.example.util.ButtonName;
import com.example.util.SendMsg;
import com.example.util.TelegramUsers;
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
            if (message.hasText()) {
                String text = message.getText();

                if (text.equals("Главное меню !") || text.equals("/start")) {
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
            }
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "*Вы ввели неверную команду*"));
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
