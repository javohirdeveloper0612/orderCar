package com.example.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InlineButton {
    public static InlineKeyboardButton button(String text, String callBackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callBackData);
        return inlineKeyboardButton;
    }

    public static List<InlineKeyboardButton> row(InlineKeyboardButton... inlineKeyboardButtons) {
        return new LinkedList<>(Arrays.asList(inlineKeyboardButtons));
    }

    @SafeVarargs
    public static List<List<InlineKeyboardButton>> rowList(List<InlineKeyboardButton>... row) {
        return new LinkedList<>(Arrays.asList(row));
    }

    public static InlineKeyboardMarkup keyboardMarkup(List<List<InlineKeyboardButton>> rowList) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rowList);
        return keyboard;
    }
}
