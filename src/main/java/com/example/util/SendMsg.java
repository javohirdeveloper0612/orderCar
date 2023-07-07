package com.example.util;

import com.example.entity.LocationClient;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.File;


public class SendMsg {
    public static SendMessage sendMsg(Long id, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText(text);
        sendMessage.setParseMode("Markdown");
        return sendMessage;
    }
    public static SendMessage sendMsg(Long id, String text,Integer messageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setReplyToMessageId(messageId);
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


    public static SendPhoto sendPhoto(Long id, String text) {
        SendPhoto sendPhoto = new SendPhoto();

        InputFile input = new InputFile(new File("attaches/price.jpg"));

        sendPhoto.setChatId(id);
        sendPhoto.setPhoto(input);
        sendPhoto.setCaption(text);

        return sendPhoto;
    }

    public static SendPhoto sendPhotoCarInfo(Long id, String text) {
        SendPhoto sendPhoto = new SendPhoto();

        InputFile input = new InputFile(new File("attaches/car-info.jpg"));

        sendPhoto.setChatId(id);
        sendPhoto.setPhoto(input);
        sendPhoto.setCaption(text);

        return sendPhoto;
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

    public static SendDocument sendDoc(Long id, InputFile inputFile) {

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(id);
        sendDocument.setDocument(inputFile);
        return sendDocument;
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

    public static SendLocation sendLocation(LocationClient location) {
        SendLocation sendLocation = new SendLocation();
        sendLocation.setChatId(1030035146L);
        sendLocation.setLatitude(location.getLatitude());
        sendLocation.setLongitude(location.getLongitude());
        return sendLocation;
    }
    public static SendLocation sendLocation(Long id ,LocationClient location,Integer messageId) {
        SendLocation sendLocation = new SendLocation();
        sendLocation.setChatId(id);
        sendLocation.setLatitude(location.getLatitude());
        sendLocation.setLongitude(location.getLongitude());
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

    public static EditMessageText editMessage(Long chatId, String text, InlineKeyboardMarkup markup,Integer messageId){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        editMessageText.setReplyMarkup(markup);

        return editMessageText;
    }

}
