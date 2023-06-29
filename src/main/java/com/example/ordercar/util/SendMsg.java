package com.example.ordercar.util;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;


public class SendMsg {
    public static SendMessage sendMsg(Long id, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText(text);
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    public static SendMessage sendMsg(Long id, String text, ReplyKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText(text);
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(markup);
        return sendMessage;
    }

    public static SendMessage sendMsg(Long id, String text, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText(text);
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(markup);
        return sendMessage;
    }

    public static SendMessage sendMsgParse(Long chatId, String text, ReplyKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }


    public static SendMessage sendMsgMark(Long chatId, String text, ReplyKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(markup);
        return sendMessage;
    }

    public static SendDocument sendDocument(Long id, String text, String inputFile) {
        SendDocument document = new SendDocument();
        InputFile input = new InputFile();
        input.setMedia(inputFile);
        document.setChatId(id);
        document.setDocument(input);
        document.setCaption(text);
        return document;
    }

    public static SendDocument sendDocument(Long id, InputFile inputFile) {
        SendDocument document = new SendDocument();
        document.setDocument(inputFile);
        document.setChatId(id);
        return document;
    }

    public static SendMessage sendMsgParse(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }

    public static SendLocation sendLocation(Long chatId, Integer messageId) {
        SendLocation sendLocation = new SendLocation();
        sendLocation.setChatId(chatId);
        sendLocation.setLatitude(41.37607);
        sendLocation.setLongitude(69.365975);
        sendLocation.setReplyToMessageId(messageId);
        return sendLocation;
    }


    public static EditMessageText sendMsgParseEdite(Long chatId, String text, Integer messageId) {
        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        sendMessage.setMessageId(messageId);
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }


    public static DeleteMessage deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(chatId);
        return deleteMessage;
    }

}
