package com.example.ordercar.admin.service;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;


@Service
public class ListOfActiveOrdersService {

    private final MyTelegramBot myTelegramBot;


    public ListOfActiveOrdersService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void listOfActiveOrders(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Active Zakaslar ro'yxatiga bo'limiga kiridiz !!! "));
    }
}
