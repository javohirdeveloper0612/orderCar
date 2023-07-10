package com.example.service;
import com.example.controller.TransportUslugaController;
import com.example.entity.OrderClientEntity;
import com.example.enums.Status;
import com.example.mytelegram.MyTelegramBot;
import com.example.repository.OrderClientRepository;
import com.example.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class TransportUslugaService {


    private final MyTelegramBot myTelegramBot;
    private final CalendarUtil calendarUtil;
    private final OrderClientRepository orderClientRepository;


    @Lazy
    @Autowired
    public TransportUslugaService(MyTelegramBot myTelegramBot, CalendarUtil calendarUtil, OrderClientRepository orderClientRepository) {
        this.myTelegramBot = myTelegramBot;
        this.calendarUtil = calendarUtil;
        this.orderClientRepository = orderClientRepository;
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
                "*4 - шаг  ⬇️\n" +
                        "\n" +
                        "- Введите свой номер телефона  ⬇️" +
                        "\n- Например : +998901234567 ✅*",
                ButtonUtil.getContact()));
    }

    public void replyStart(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("*1 - шаг  ⬇️\n" +
                "\n" +
                "- Пожалуйста, выберите дату, которую вы хотите заказать  ⬇️*");
        sendMessage.setParseMode("Markdown");
        sendMessage.setReplyMarkup(
                calendarUtil.makeYearKeyBoard(
                        LocalDate.now().getYear(), LocalDate.now().getMonthValue()));
        myTelegramBot.send(sendMessage);
    }

    public void getFullName(Long chatId) {
        myTelegramBot.send(SendMsg.sendMsgParse(chatId,
                "*5 - шаг  ⬇️\n" +
                        "\n" +
                        "- Пожалуйста, введите ваше полное имя\n" +
                        "Например : Коржабов Шахзод* ✅"));
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

    public boolean checkPhone(Message message) {
        if (!message.getText().startsWith("+998") || message.getText().length() != 13) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Пожалуйста, введите номер телефона в форму ниже!*" +
                            "*\nНапример : +998901234567  ✅*"));
            return false;
        }

        for (int i = 0; i < message.getText().length(); i++) {
            if (Character.isAlphabetic(message.getText().charAt(i))) {
                myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                        "*Пожалуйста, введите номер телефона в форму ниже!*" +
                                "*\nНапример : +998901234567  ✅*"));
                return false;
            }
        }

        return true;
    }

    public void getFromWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*2 - шаг  ⬇️\n" +
                        "\n" +
                        "- Введите адрес, по которому машина заберет груз  ⬇️\n" +
                        "- Воспользуйтесь разделом «Местоположение Telegram»  \uD83D\uDCCD*"));

    }

    public void getToWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*3 - шаг  ⬇️\n" +
                        "\n" +
                        "- Введите адрес, куда машина доставит товар  ⬇️\n" +
                        "- Воспользуйтесь разделом «Местоположение Telegram»  \uD83D\uDCCD*"));
    }

    public void sendContact(Message message) {

        String randomNumber = RandomUtil.getRandomNumber();
        String phone = message.getContact().getPhoneNumber();

        Optional<OrderClientEntity> optional = orderClientRepository.findTop1ByChatIdAndStatusAndIsVisibleTrueAndPhoneIsNullOrderByOrderDateDesc(message.getChatId(), Status.NOTACTIVE);
        if (optional.isEmpty()) {
            return;
        }
        OrderClientEntity orderClient = optional.get();

        orderClient.setSmsCode(randomNumber);
        orderClient.setPhone(phone);
        orderClient.setChatId(message.getChatId());
        orderClientRepository.save(orderClient);
        SmsServiceUtil.sendSmsCode(SmsServiceUtil.removePlusSign(phone), randomNumber);
        SendMessage sendMessage = SendMsg.sendMsgParse(message.getChatId(),

                "*- код  ⬇️\n" +
                        "\n" +
                        "➡️ +" + phone + "*" + "* на этот номер отправлен код подтверждения  ✅*" +
                        "*\n- Пожалуйста, введите проверочный код ⬇️*");
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardRemove);
        myTelegramBot.send(sendMessage);

    }

    public void getPayment(Long chatId, Long orderId) {

        myTelegramBot.send(SendMsg.sendMsg(chatId,
                "*6 -  шаг  ⬇️\n" +
                        "\n" +
                        "Пожалуйста, выберите удобную для вас платежную систему*",
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  PAYME (Автоплатеж)", "payme#" + orderId)),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  НАЛИЧНЫЕ ", "naqd#" + orderId)),
                        InlineButton.row(InlineButton.button("⬅️ НАЗАД", "back"))
                ))));
    }


    public TelegramUsers saveUser(Long chatId) {

        for (TelegramUsers users : TransportUslugaController.usersList) {
            if (users.getChatId().equals(chatId)) {
                return users;
            }
        }
        var users = new TelegramUsers();
        users.setChatId(chatId);
        TransportUslugaController.usersList.add(users);
        return users;
    }
}
