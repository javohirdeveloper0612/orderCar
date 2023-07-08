package com.example.service;
import com.example.enums.Status;
import com.example.mytelegram.MyTelegramBot;
import com.example.payme.util.PaymentUtil;
import com.example.repository.OrderClientRepository;
import com.example.util.InlineButton;
import com.example.util.SendMsg;
import com.example.entity.OrderClientEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Optional;

@Service
public class OrderClientService {

    private final MyTelegramBot myTelegramBot;
    private final PaymentUtil paymentUtil;
    private final OrderClientRepository orderClientRepository;

    public OrderClientService(MyTelegramBot myTelegramBot, PaymentUtil paymentUtil, OrderClientRepository orderClientRepository) {
        this.myTelegramBot = myTelegramBot;
        this.paymentUtil = paymentUtil;
        this.orderClientRepository = orderClientRepository;
    }


    public void getPayment(Message message, Long id) {
        Optional<OrderClientEntity> optional = orderClientRepository.findById(id);
        if (optional.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Order not found"));
            return;
        }
        OrderClientEntity orderClient = optional.get();
        if (orderClient.getStatus() == Status.ACTIVE) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Order already payed"));
            return;
        }

        orderClientRepository.save(orderClient);
        InlineKeyboardButton button = new InlineKeyboardButton("▶️ Оплата");
        button.setUrl(paymentUtil.generatePaymentUrl(id,orderClient.getPhone(), orderClient.getAmount()));
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️ Произведите оплату, перейдя по ссылке ниже и нажмите кнопку ✅ Проверить:*",
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(button)
                )))

        );
    }
}
