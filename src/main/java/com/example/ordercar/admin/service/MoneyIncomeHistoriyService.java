package com.example.ordercar.admin.service;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class MoneyIncomeHistoriyService {

    private final MyTelegramBot myTelegramBot;

    public MoneyIncomeHistoriyService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void moneyIncomeHistoriy(Message message) {


        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Pul daromad tarixi bo'limiga kiridiz !!! "));
    }
}
