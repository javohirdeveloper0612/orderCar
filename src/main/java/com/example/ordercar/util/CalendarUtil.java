package com.example.ordercar.util;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class CalendarUtil {
    private final ButtonUtil buttonUtil;
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    private final List<Integer> months30 = new ArrayList<>(List.of(4, 6, 9, 11));

    public CalendarUtil() {
        this.buttonUtil = new ButtonUtil();
    }

    private InlineKeyboardMarkup makeDayButton(int year, int month) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new LinkedList<>();

        int maxDay = getMaxDay(month, year);
        int day = getStartDay(month, year);
        int maxRow = maxDay == 31 ? 6 : 5;

        for (int i = 0; i < maxRow; i++) {
            List<InlineKeyboardButton> row = new LinkedList<>();
            for (int j = 0; j < 6 && day != maxDay + 1; j++) {
                if (isBooked(LocalDate.of(year, month, day))) {
                    continue;
                }
                row.add(buttonUtil.button(day + "", "/day/" + year + "/" + month + "/" + day));
                day++;
            }
            keyboard.add(row);
        }

        markup.setKeyboard(keyboard);
        return markup;
    }

    private boolean isBooked(LocalDate date) {
        //you should to check  date is busy
        return false;
    }

    private int getStartDay(int month, int year) {
        return year != LocalDate.now().getYear() || month != LocalDate.now().getMonthValue() ? 1 : LocalDate.now().getDayOfMonth();
    }

    private int getMaxDay(int month, int year) {
        if (month == 2) {
            return LocalDate.of(year, 1, 1).isLeapYear() ? 29 : 28;
        }
        return months30.contains(month) ? 30 : 31;
    }


    public InlineKeyboardMarkup makeMonthButton(int year, int month) {
        int prev = month - 1;
        int prevYear = year;
        if (prev == 0) {
            prev = 12;
            prevYear--;
        }
        int next = month + 1;
        int nextYear = year;
        if (next > 12) {
            nextYear++;
            next %= 12;
        }

        InlineKeyboardMarkup markup = makeDayButton(year, month);
        List<List<InlineKeyboardButton>> keyboard = markup.getKeyboard();
        List<InlineKeyboardButton> lastRow = new LinkedList<>();

        if ((year != LocalDate.now().getYear() || month != LocalDate.now().getMonthValue())) {
            lastRow.add(buttonUtil.button("<<", "/month/" + prevYear + "/" + prev));
        }
        lastRow.add(buttonUtil.button(months[month - 1], "/"));
        lastRow.add(buttonUtil.button(">>", "/month/" + nextYear + "/" + next));

        keyboard.add(lastRow);
        return markup;
    }

    public InlineKeyboardMarkup makeYearKeyBoard(int year, int month) {
        InlineKeyboardMarkup markup = makeMonthButton(year, month);
        List<List<InlineKeyboardButton>> keyboard = markup.getKeyboard();

        List<InlineKeyboardButton> lasRow = new LinkedList<>();
        if (year > LocalDate.now().getYear()) {
            lasRow.add(buttonUtil.button("<<", "/year/" + (year - 1) + "/" + month));
        }
        lasRow.add(buttonUtil.button(String.valueOf(year), "/"));
        lasRow.add(buttonUtil.button(">>", "/year/" + (year + 1) + "/" + month));
        keyboard.add(lasRow);
        return markup;
    }

}
