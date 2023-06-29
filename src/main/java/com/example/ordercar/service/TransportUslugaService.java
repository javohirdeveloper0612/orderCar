package com.example.ordercar.service;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;

@Service
public class TransportUslugaService {


    private final MyTelegramBot myTelegramBot;
    private final CalendarUtil calendarUtil;

    @Lazy
    @Autowired
    public TransportUslugaService(MyTelegramBot myTelegramBot, CalendarUtil calendarUtil) {
        this.myTelegramBot = myTelegramBot;
        this.calendarUtil = calendarUtil;
    }

    public void priceData(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "*Ushbu funksiya tez orada ishga tushadi *")
        );
    }

    public void orderCar(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Iltimos telefon raqamingizni quyidagi shakilda kiriting : " +
                        "\nMasalan : +998901234567 ✅*",
                ButtonUtil.getContact()));
    }

    public void replyStart(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*Iltimos, buyurtma bermoqchi bo'lgan sanani tanlang!*");
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(calendarUtil.makeYearKeyBoard(LocalDate.now().getYear(), LocalDate.now().getMonthValue()));
        myTelegramBot.send(sendMessage);
    }


    public void documentData(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "*Документ меню*",
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
