package com.example.ordercar.admin.service;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class PhoneOrderService {

    private MyTelegramBot myTelegramBot;

    public PhoneOrderService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void phoneOrder(Message message) {


        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Telefon orqali zakas qilish bo'limiga kiridiz !!! "));
    }
}
