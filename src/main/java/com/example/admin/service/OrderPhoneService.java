package com.example.admin.service;
import com.example.mytelegram.MyTelegramBot;
import com.example.util.Button;
import com.example.util.ButtonName;
import com.example.util.CalendarUtil;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.time.LocalDate;


@Service
public class OrderPhoneService {

    private final MyTelegramBot myTelegramBot;
    private final CalendarUtil calendarUtil;

    public OrderPhoneService(MyTelegramBot myTelegramBot, CalendarUtil calendarUtil) {
        this.myTelegramBot = myTelegramBot;
        this.calendarUtil = calendarUtil;
    }

    public void onlineOrder(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️\n" +
                        "\n" +
                        "- Пожалуйста, введите свой номер телефона в форму ниже, чтобы зарегистрироваться*" +
                        "\n*Например : +998971234567 ✅*"));
    }

    public void getFullNameOffline(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️\n" +
                        "\n" +
                        "- Пожалуйста, введите ваше имя и фамилию для регистрации*" +
                        "\n* Например: Ismatov Hamdam ✅*"));
    }

    public void getMoney(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "⬇️\n" +
                        "\n" +
                        "*- Рассчитанные километры от пункта А до пункта Б Введите сумму денег* \uD83D\uDCB8"));
    }

    public void replyStartAdmin(Long chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("⬇️\n" +
                "\n" +
                "*- Пожалуйста, выберите дату, которую вы хотите заказать  ⬇️*");
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(calendarUtil.makeYearKeyBoard(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue()));
        myTelegramBot.send(sendMessage);

    }

    public void guiderOrderPhoneMoney(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "⬇️\n" +
                        "\n" +
                        "*- Руководство по расчету денег*  \uD83D\uDD8A\n" +
                        "\n" +
                        "*- Когда заказчик назовет адрес, откуда нужно забрать груз и доставить груз, вы через навигатор узнаете, сколько километров до места назначения, посчитаете деньги и введете в бота*  ⬇️\n" +
                        "\n" +
                        "*- Образец : 1250000  ✅*"));
    }

    public void saveOrderPhone(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "⬇️\n" +
                        "\n" +
                        "*- Заказ принят*  ✅"));

    }
}
