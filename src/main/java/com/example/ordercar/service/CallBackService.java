package com.example.ordercar.service;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.CalendarUtil;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;

@Component
public class CallBackService {
    private final CalendarUtil calendarUtil;
    private final MyTelegramBot myTelegramBot;


    public CallBackService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
        calendarUtil = new CalendarUtil();
    }

    public void sendCalendar(Message message, int year, int month) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId());
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setText("*Iltimos, buyurtma bermoqchi bo'lgan sanani tanlang!*");
        editMessageText.setParseMode("Markdown");
        editMessageText.setReplyMarkup(calendarUtil.makeYearKeyBoard(year, month));
        myTelegramBot.send(editMessageText);
    }

    public LocalDate getDate(Message message, int year, int month, int day) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(message.getChatId());
        myTelegramBot.send(deleteMessage);
        return LocalDate.of(year, month, day);
    }

    public void getPayMe(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParseEdite(message.getChatId(),
                "\uD83D\uDD22 *To'lov miqdorini kiriting minimal 1000: (UZS)*", message.getMessageId()));
    }

    public void getClick(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Hozircha botimiz Clickni qo'llab quvvatlamaydi *" +
                        " *Tez orada ushbu funksiya ishga tushadi *"));

    }

    public void getHumo(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Hozircha botimiz Humoni qo'llab quvvatlamaydi.*" +
                        " * Tez orada ushbu funksiya ishga tushadi *"));
    }

    public void getUzum(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Hozircha botimiz Uzumni qo'llab quvvatlamaydi.*" +
                        " * Tez orada ushbu funksiya ishga tushadi *"));

    }


}