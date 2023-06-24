package com.example.ordercar.service;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.Button;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.SendMsg;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class MainService {

    private final MyTelegramBot myTelegramBot;

    @Lazy
    public MainService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void mainMenu(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Здравствуйте ! Выберите необходимое меню ",
                Button.markup(
                        Button.rowList(
                                Button.row(Button.button(ButtonName.transportusluga), Button.button(ButtonName.metallprokat)),
                                Button.row(Button.button(ButtonName.metallBuyum), Button.button("?"))
                        ))));
    }

    public void transportMenu(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Транспорт услуга меню"
                , Button.markup(Button.rowList(
                        Button.row(Button.button(ButtonName.priceList),
                                Button.button(ButtonName.orderCar)),
                        Button.row(Button.button(ButtonName.document),
                                Button.button(ButtonName.backMainMenu))
                ))));
    }

    public void metallBuyumMenu(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Metall buyum ishladi"));
    }

    public void metalProkatMenu(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "metall prokat usluga ishladi"));
    }

    public void help(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsgParse(message.getChatId(),
                        "Hozircha yordam mavjud emas"));
    }

    public void priceData(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "Price data ishladi")
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

    public void orderCar(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "order car data ishladi")
        );
    }

    public void dataCar(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "Data car data ishladi")
        );
    }

    public void dataVoditel(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "Data Voditel  data ishladi")
        );
    }
}

