package com.example.admin.service;
import com.example.entity.OrderClientEntity;
import com.example.enums.Status;
import com.example.mytelegram.MyTelegramBot;
import com.example.repository.OrderClientRepository;
import com.example.util.CalendarUtil;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.List;


@Service
public class UpdateDayService {

    private final MyTelegramBot myTelegramBot;
    private final CalendarUtil calendarUtil;
    private final OrderClientRepository orderClientRepository;


    public UpdateDayService(MyTelegramBot myTelegramBot,
                            CalendarUtil calendarUtil,
                            OrderClientRepository orderClientRepository) {

        this.myTelegramBot = myTelegramBot;
        this.calendarUtil = calendarUtil;
        this.orderClientRepository = orderClientRepository;
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


    public void updateOrderList(Message message) {

            List<OrderClientEntity> orderClientEntityList =
                    orderClientRepository.findAllByStatus(Status.ACTIVE);

            if(orderClientEntityList.isEmpty()){

                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                        "⬇️\n" +
                                "\n" +
                                "*- Нет АCTIVE заказов для удаления * ❌"));


            }

            for (OrderClientEntity orderClientEntity : orderClientEntityList) {

                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                        "⬇️\n" +
                                "\n" +
                                "➡️   *Активный заказ*\n" +
                                "\n" +
                                "*\uD83D\uDD37  ID Номер  :  *" + orderClientEntity.getId() + "\n" +
                                "*\uD83D\uDD37  Имя и фамилия  *:  " + orderClientEntity.getFullName() + "\n" +
                                "*\uD83D\uDD37  Номер телефона  *:  " + orderClientEntity.getPhone() + "\n" +
                                "*\uD83D\uDD37  Дата заказа  :  *" + orderClientEntity.getOrderDate() + "\n" +
                                "*\uD83D\uDD37  Способ оплаты  *:  " + orderClientEntity.getPayment() + "\n" +
                                "*\uD83D\uDD37  Статус заказа  *:  " + orderClientEntity.getStatus()));

            }
        }

    public void updateOrderId(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "⬇️\n" +
                        "\n" +
                        "*- Введите << ID >> номер заказа, для которого вы хотите изменить дату заказа*"));
    }
}
