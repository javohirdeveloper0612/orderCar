package com.example.admin.util;

import com.example.mytelegram.MyTelegramBot;
import com.example.util.Button;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class ButtonAdmin {

    private final MyTelegramBot myTelegramBot;

    public ButtonAdmin(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }


    public void mainMenu(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Добро пожаловать в админку, выбирайте нужный вам раздел! *",

                Button.markup(com.example.util.Button.rowList(
                        Button.row(
                                Button.button(ButtonNameAdmin.onlineOrder)),
                        Button.row(
                                Button.button(ButtonNameAdmin.activeOrder),
                                Button.button(ButtonNameAdmin.notactiveOrder)
                        ),
                        Button.row(
                                Button.button(ButtonNameAdmin.deleteOrder),
                                Button.button(ButtonNameAdmin.updateDayOrder)
                        ),
                        Button.row(
                                Button.button(ButtonNameAdmin.setting))
                ))));

    }



}
