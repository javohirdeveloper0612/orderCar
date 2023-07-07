package com.example.service;
import com.example.mytelegram.MyTelegramBot;
import com.example.util.CalendarUtil;
import com.example.util.SendMsg;
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
        editMessageText.setText("*Пожалуйста, выберите дату, которую вы хотите заказать !*");
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



    public void getClick(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*В настоящее время наш бот не поддерживает Click *" +
                        " *Эта функция будет доступна в ближайшее время ! *"));

    }

    public void getHumo(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*В настоящее время наш бот не поддерживает Humo*" +
                        " * Эта функция будет доступна в ближайшее время ! *"));
    }

    public void getUzum(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*В настоящее время наш бот не поддерживает УЗУМ*" +
                        " * Эта функция будет доступна в ближайшее время !  *"));

    }


}