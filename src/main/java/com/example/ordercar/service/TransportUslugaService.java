package com.example.ordercar.service;

import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.*;
import com.example.ordercar.util.Button;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.ButtonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;

import static com.example.ordercar.controller.TransportUslugaController.usersList;

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
                SendMsg.sendDocument(message.getChatId(), "Информация о цене !",
                        "BQACAgIAAxkBAAIVmWSmn7YGSxSMCFkjhmGywXYKhsmHAAMsAAI2GjlJJqfaMlcTXVYvBA")
        );
    }

    public void orderCar(Long chatId) {
        TelegramUsers user = saveUser(chatId);
        user.setStep(Step.GETPHONE);

        myTelegramBot.send(SendMsg.sendMsgParse(chatId,
                "*Пожалуйста, введите свой номер телефона в форму ниже : " +
                        "\nНапример : +998901234567 ✅*",
                ButtonUtil.getContact()));
    }

    public void replyStart(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*Пожалуйста, выберите дату, которую вы хотите заказать!*");
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(calendarUtil.makeYearKeyBoard(LocalDate.now().getYear(), LocalDate.now().getMonthValue()));
        myTelegramBot.send(sendMessage);
    }


    public void documentData(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "*Документ меню*",
                        Button.markup(
                                Button.rowList(Button.row(
                                                Button.button(ButtonName.dataDriver),
                                                Button.button(ButtonName.dataCar)
                                        ),
                                        Button.row(Button.button(ButtonName.backTransportMenu))
                                )))
        );
    }

    public void guideOrderCar(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Инструкции по заказу  ❕❕❕\n" +
                "\n" +
                "\uD83D\uDD35 1 - Выберите день, в который хотите заказать автомобиль\n" +
                "\uD83D\uDD35 2 - Введите точный адрес точки отправки груза используя функцию «Telegram \uD83D\uDCCD Location»\n" +
                "\uD83D\uDD35 3 - Введите точный адрес точки доставки груза используя функцию «Telegram \uD83D\uDCCD Location»\n" +
                "\uD83D\uDD35 4 - Введите номер телефона и подтвердите через SMS\n" +
                "\uD83D\uDD35 5 - Введите полное имя и фамилию\n" +
                "\uD83D\uDD35 6 - Выберите удобный для вас вид оплаты  \n" +
                "\uD83D\uDD35 7 - Оплатить указанную цену  \n" +
                "\uD83D\uDD35 Мы свяжемся с вами, как только ваш заказ будет успешно выполнен  \uD83E\uDD1D\n" +
                "\n" +
                "Осторожность  ❗️❗️❗️\n" +
                "\n" +
                "⛔️ Введите правильный номер телефона, там код подтверждения смс  \n" +
                "⛔️ При отправке адреса пожалуйста будьте бдительны, необходимо указать правильный адрес, так как машина будет двигаться по указанному Вами адресу  \n" +
                "⛔️ Дата, выбранная вами в календаре, не изменится. Будьте внимательны при выборе даты  \n" +
                "⛔️ Пожалуйста, будьте внимательны при заказе  \uD83D\uDCAF\n" +
                "\n" +
                "-. Вы можете продолжить заказ  ✅⬇️"));

    }

    public TelegramUsers saveUser(Long chatId) {

        for (TelegramUsers users : usersList) {
            if (users.getChatId().equals(chatId)) {
                return users;
            }
        }
        var users = new TelegramUsers();
        users.setChatId(chatId);
        usersList.add(users);
        return users;
    }
}
