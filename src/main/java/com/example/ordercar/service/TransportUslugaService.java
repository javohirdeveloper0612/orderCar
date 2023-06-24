package com.example.ordercar.service;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.Button;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.SendMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class TransportUslugaService {


    private final MyTelegramBot myTelegramBot;

    @Lazy
    @Autowired
    public TransportUslugaService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void priceData(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "Price data ishladi")
        );
    }

    public void orderCar(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "order car data ishladi")
        );
    }

    public void documentData(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "Документ меню",
                        Button.markup(
                                Button.rowList(Button.row(
                                                Button.button(ButtonName.dataVoditel),
                                                Button.button(ButtonName.dataCar)
                                        ),
                                        Button.row(Button.button(ButtonName.backTransportMenu))
                                )))
        );
    }
}
