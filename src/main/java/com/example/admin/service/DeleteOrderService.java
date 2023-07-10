package com.example.admin.service;
import com.example.entity.OrderClientEntity;
import com.example.enums.Status;
import com.example.mytelegram.MyTelegramBot;
import com.example.repository.OrderClientRepository;
import com.example.util.Button;
import com.example.util.ButtonName;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.List;
import java.util.Optional;

@Service
public class DeleteOrderService {

    private final MyTelegramBot myTelegramBot;
    private final OrderClientRepository orderClientRepository;


    public DeleteOrderService(MyTelegramBot myTelegramBot,
                              OrderClientRepository orderClientRepository) {

        this.myTelegramBot = myTelegramBot;
        this.orderClientRepository = orderClientRepository;
    }

    public void deleteOrderList(Message message) {

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

    public void deleteOrderGetId(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "⬇️\n" +
                        "\n" +
                        "*- Введите << ID >> номер заказа, который вы хотите удалить * ⬇️"));

    }

    public void deleteOrderById(Message message) {

        Optional<OrderClientEntity> optional =
                orderClientRepository.findById(Long.valueOf(message.getText()));

        OrderClientEntity orderClient = optional.get();
        orderClient.setStatus(Status.BLOCK);
        orderClientRepository.save(orderClient);

    }

    public void deleteOrder(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "⬇️\n" +
                        "\n" +
                        "* - Заказ был удален * \uD83D\uDDD1 ",
                Button.markup(
                        Button.rowList(
                                Button.row(
                                        Button.button(ButtonName.backMainMenu)
                                )
                        )
                )));
    }
}
