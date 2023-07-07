package com.example.ordercar.service;

import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.enums.Payment;
import com.example.ordercar.enums.Status;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.payme.util.PaymentUtil;
import com.example.ordercar.repository.OrderClientRepository;
import com.example.ordercar.util.InlineButton;
import com.example.ordercar.util.SendMsg;
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

    public void getFullName(Long chatId) {
        myTelegramBot.send(SendMsg.sendMsgParse(chatId,
                "*Пожалуйста, введите ваше полное имя \n" +
                        "Например: Коржабов Шахзод* ✅"));
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
        orderClient.setPayment(Payment.PLASTIK);
        orderClientRepository.save(orderClient);
        InlineKeyboardButton button = new InlineKeyboardButton("▶️ To'lov qilish");
        button.setUrl(paymentUtil.generatePaymentUrl(id, orderClient.getAmount()));
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️ Произведите оплату, перейдя по ссылке ниже и нажмите кнопку ✅ Проверить:*",
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(button)
                )))

        );
    }
}
