package com.example.admin.service;
import com.example.mytelegram.MyTelegramBot;
import com.example.util.CalendarUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;


@Service
public class UpdateDayService {

    private final MyTelegramBot myTelegramBot;
    private final CalendarUtil calendarUtil;


    public UpdateDayService(MyTelegramBot myTelegramBot,
                            CalendarUtil calendarUtil) {

        this.myTelegramBot = myTelegramBot;
        this.calendarUtil = calendarUtil;
    }

    public void replyStartUpdateOrder(Long chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*⬇️\n" +
                "\n" +
                "- Выберите день, в который вы хотите изменить дату заказа  ⬇️*");
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(calendarUtil.makeYearKeyBoard(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue()));
        myTelegramBot.send(sendMessage);

    }


}
