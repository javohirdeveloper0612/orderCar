package com.example.ordercar.controller;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.service.DriverService;
import com.example.ordercar.util.ButtonName;
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

            var transportStep = saveUser(message.getChatId());
            if (message.hasText()) {
                String text = message.getText();

                if (text.equals("loca")){

                    double latitude = 41.37607;
                    double longitude = 69.365975;

// Location obyekti yaratish
                    Location location = new Location();
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);

// Caption yaratish
                    String caption = "Joylashuv: " + latitude + ", " + longitude;

// Inline keyboard yaratish
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText("Joylashuvni ko'rsatish");
                    button.setCallbackData("show_location#" + latitude + "#" + longitude);
                    row.add(button);
                    inlineKeyboardMarkup.setKeyboard(Collections.singletonList(row));

// Javob xabarini yaratish
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                    sendMessage.setText("Joylashuvni ko'rsatish uchun tugmani bosing");
                    sendMessage.setChatId(message.getChatId());

                    myTelegramBot.send(sendMessage);

                }
                switch (text) {
                    case "/start" -> {
                        driverService.menu(message);
                    }

                    case ButtonName.activeOrder -> {
                        driverService.activeOrder(message);
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
