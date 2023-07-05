package com.example.ordercar.service;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.InlineButton;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderClientService {

    private final MyTelegramBot myTelegramBot;

    public OrderClientService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void getFullName(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Пожалуйста, введите ваше полное имя \n" +
                        "Например: Коржабов Шахзод*"));
    }

    public ReplyKeyboardMarkup getLocation() {
        KeyboardButton locationButton = new KeyboardButton();
        locationButton.setText("Share  location \uD83D\uDCCD");
        locationButton.setRequestLocation(true);
        KeyboardRow row = new KeyboardRow();
        row.add(locationButton);
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setKeyboard(keyboard);
        replyMarkup.setResizeKeyboard(true);
        return replyMarkup;
    }

    public void getPayment(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️ Произведите оплату, перейдя по ссылке ниже и нажмите кнопку ✅ Проверить:*",
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("▶️ To'lov qilish", "payment")),
                        InlineButton.row(InlineButton.button("✅ Tekshirish", "claim")),
                        InlineButton.row()

                )))

        );
    }
}
