package com.example.ordercar.admin.service;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class CompletedOrderListService {

    private final MyTelegramBot myTelegramBot;

    public CompletedOrderListService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void completedOrderList(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                " Yakunlangan zakaslar bo'limiga kiridiz !!! Excel variantda tashlanadi malumotlat !!! "));
    }

}
