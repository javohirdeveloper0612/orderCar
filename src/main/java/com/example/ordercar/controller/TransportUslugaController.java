package com.example.ordercar.controller;

import com.example.ordercar.entity.LocationClient;
import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.enums.Payment;
import com.example.ordercar.enums.ProfileStatus;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.repository.OderClientRepository;
import com.example.ordercar.service.MainService;
import com.example.ordercar.service.OrderClientService;
import com.example.ordercar.service.TransportUslugaService;
import com.example.ordercar.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TransportUslugaController {

    private final List<TelegramUsers> usersList = new ArrayList<>();
    private final MainService mainService;
    public OrderClientEntity orderClient = new OrderClientEntity();
    private final MainController mainController;
    private final TransportUslugaService transportService;
    final OrderClientService orderClientService;
    private final OderClientRepository oderClientRepository;
    private final MyTelegramBot myTelegramBot;


    @Autowired
    public TransportUslugaController(MainService mainService,
                                     MainController mainController,
                                     TransportUslugaService transportService,
                                     OrderClientService orderClientService,
                                     OderClientRepository oderClientRepository,
                                     MyTelegramBot myTelegramBot) {
        this.mainService = mainService;
        this.mainController = mainController;
        this.transportService = transportService;
        this.orderClientService = orderClientService;
        this.oderClientRepository = oderClientRepository;
        this.myTelegramBot = myTelegramBot;


    }


    public void handler(Message message) {
        var transportStep = saveUser(message.getChatId());
        var mainStep = mainController.saveUser(message.getChatId());

        if (message.hasText()) {

            if (transportStep.getStep() == null) {
                transportStep.setStep(Step.TRANSPORT);
            }

            switch (transportStep.getStep()) {
                case TRANSPORT -> {
                    switch (message.getText()) {
                        case ButtonName.priceList -> transportService.priceData(message);
                        case ButtonName.orderCar -> {
                            transportService.orderCar(message);
                            transportStep.setStep(Step.GETPHONE);
                        }
                        case ButtonName.document -> {
                            transportService.documentData(message);
                            transportStep.setStep(Step.DOCUMENT);
                        }
                        case ButtonName.backMainMenu -> {
                            mainService.mainMenu(message);
                            mainStep.setStep(Step.MAIN);
                        }

                    }
                }
                case DOCUMENT -> {
                    switch (message.getText()) {
                        case ButtonName.backTransportMenu -> {
                            mainService.transportMenu(message);
                            transportStep.setStep(Step.TRANSPORT);
                        }
                        case ButtonName.dataCar -> mainService.dataCar(message);
                        case ButtonName.dataVoditel -> mainService.dataVoditel(message);
                    }
                }
                case GETPHONE -> {
                    sendSmsCode(message);
                    transportStep.setStep(Step.CHECKSMS);
                }
                case CHECKSMS -> {
                    if (checkSmsCode(message)) {
                        orderClientService.getFullName(message);
                        transportStep.setStep(Step.GETFULLNAME);
                    } else {
                        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                                "*Tasdiqlash kodi xato. Iltimos, qayta kiriting*"));
                    }
                }
                case GETFULLNAME -> {
                    orderClient.setFullName(message.getText());
                    transportService.replyStart(message.getChatId());
                }

                case PAYMENT -> orderClientService.getPayment(message);

            }
        } else if (message.hasContact()) {
            sendContact(message);
            transportStep.setStep(Step.CHECKSMS);
        } else if (message.hasLocation()) {
            orderClient.setFromWhere(getCurrentLocation(message));
            getPayment(message);
        }
    }

    private void getPayment(Message message) {
        System.out.println("Mazgi");
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Iltimos o'zingizga qulay bo'lgan to'lov tizimini tanlang",
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  PAYME(Avto to'lov)", "payme")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  CLICK", "click")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  HUMO KARTA", "humo")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  UZUM", "uzum")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  NAQD PUL", "naqd")),
                        InlineButton.row(InlineButton.button("back\uD83D\uDD19", "back"))
                ))));
    }

    public LocationClient getCurrentLocation(Message message) {
        Double latitude = message.getLocation().getLatitude();
        Double longitude = message.getLocation().getLongitude();
        LocationClient locationClient = new LocationClient();
        locationClient.setLongitude(longitude);
        locationClient.setLatitude(latitude);
        return locationClient;

    }

    boolean checkSmsCode(Message message) {
        return message.getText().equals(orderClient.getSmsCode());
    }

    public void getOrderDate(LocalDate localDate, Message message) {
        Boolean exists = oderClientRepository.existsByOrderDate(localDate);
        if (exists) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Kechirasiz, ushbu  sana band qilingan. Boshqa sanani tanlang!*"));
            transportService.replyStart(message.getChatId());

        } else {
            orderClient.setOrderDate(localDate);
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(), "*Mashina qayerda yo'lga chiqadi ? Iltimos locatsiya ulashing !*",
                    orderClientService.getLocation()));
            saveUser(message.getChatId()).setStep(null);
        }
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

    public void sendSmsCode(Message message) {
        String randomNumber = RandomUtil.getRandomNumber();
        String phone = message.getText();
        orderClient.setSmsCode(randomNumber);
        orderClient.setPhone(phone);
        SmsServiceUtil.sendSmsCode(SmsServiceUtil.removePlusSign(phone), randomNumber);
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                phone + " ushbu raqamga tasdiqlash kodi yuborildi !" +
                        "\nIltimos tasdiqlash kodini kiriting"));

    }


    public void sendContact(Message message) {
        String randomNumber = RandomUtil.getRandomNumber();
        String phone = message.getContact().getPhoneNumber();
        orderClient.setSmsCode(randomNumber);
        orderClient.setPhone(phone);
        orderClient.setChatId(message.getChatId());
        SmsServiceUtil.sendSmsCode(SmsServiceUtil.removePlusSign(phone), randomNumber);
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                phone + " ushbu raqamga tasdiqlash kodi yuborildi !" +
                        "\nIltimos tasdiqlash kodini kiriting"));

    }

    public void getCash(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Buyurtmangiz qabul qilindi ! Tez orada mutaxasislarimisz siz bilan boglanadi*"));
        orderClient.setCashOrOnline(Payment.CASH);
        orderClient.setStatus(ProfileStatus.ACTIVE);
        saveUser(message.getChatId()).setStep(null);
        oderClientRepository.save(orderClient);
        sendOrder(message);
        orderClient = new OrderClientEntity();
    }

    public void sendOrder(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(1030035146L,
                "Buyurtma : \n" +
                        "ISM VA FAMILIYA : " + orderClient.getFullName() + "" +
                        "\nTELEFON RAQAM : " + orderClient.getPhone() + "" +
                        "\nBuyurtma sanasi : " + orderClient.getOrderDate() + "" +
                        "\nStatus : " + orderClient.getStatus() + "" +
                        "\nTo'lov turi : " + orderClient.getCashOrOnline()));

        myTelegramBot.send(SendMsg.sendLocation(orderClient.getFromWhere()));

    }
}
