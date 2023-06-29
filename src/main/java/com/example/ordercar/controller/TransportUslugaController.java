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
                    if (checkPhone(message)) {
                        sendSmsCode(message);
                        transportStep.setStep(Step.CHECKSMS);
                    }
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

                case PAYMENT -> {
                    if (checkMoney(message)) {
                        orderClientService.getPayment(message);
                    }
                }
                case GETTOWHERELOCATION -> {
                    getToWhereLocation(message);
                }

                case GETFROMWHERELOCATION -> {
                    getFromWhereLocation(message);
                }

            }
        } else if (message.hasContact()) {
            sendContact(message);
            transportStep.setStep(Step.CHECKSMS);
        } else if (message.hasLocation()) {
            if (transportStep.getStep().equals(Step.GETTOWHERELOCATION)) {
                orderClient.setToWhere(getCurrentLocation(message));
                transportStep.setStep(null);
                getPayment(message);
            } else {
                orderClient.setFromWhere(getCurrentLocation(message));
                getToWhereLocation(message);
                saveUser(message.getChatId()).setStep(Step.GETTOWHERELOCATION);
            }


        }
    }

    private boolean checkMoney(Message message) {
        String text = message.getText();
        for (int i = 0; i < text.length(); i++) {
            if (Character.isAlphabetic(text.charAt(i)) || text.length() < 4) {
                myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                        "\uD83D\uDD22 *To'lov miqdorini kiriting minimal 1000: (UZS)*"));
                return false;
            }
        }

        return true;
    }

    private void getToWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Mashina qayerga boradi ? Iltimos locatsiya ulashing !*"));
    }

    private void getPayment(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Iltimos o'zingizga qulay bo'lgan to'lov tizimini tanlang*",
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  PAYME(Avto to'lov)", "payme")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  CLICK", "click")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  HUMO KARTA", "humo")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  UZUM", "uzum")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  NAQD PUL", "naqd")),
                        InlineButton.row(InlineButton.button("ORQAGA \uD83D\uDD19", "back"))
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
            getFromWhereLocation(message);
            saveUser(message.getChatId()).setStep(Step.GETFROMWHERELOCATION);
            orderClient.setOrderDate(localDate);
        }
    }

    public void getFromWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(), "*Mashina qayerdan yo'lga chiqadi ? Iltimos locatsiya ulashing !*"));

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
                "*" + phone + "*" + "* ushbu raqamga tasdiqlash kodi yuborildi !*" +
                        "*\nIltimos tasdiqlash kodini kiriting  ✅*"));
    }


    public void sendContact(Message message) {
        String randomNumber = RandomUtil.getRandomNumber();
        String phone = message.getContact().getPhoneNumber();
        orderClient.setSmsCode(randomNumber);
        orderClient.setPhone(phone);
        orderClient.setChatId(message.getChatId());
        SmsServiceUtil.sendSmsCode(SmsServiceUtil.removePlusSign(phone), randomNumber);
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*" + phone + "*" + "* ushbu raqamga tasdiqlash kodi yuborildi !*" +
                        "*\nIltimos tasdiqlash kodini kiriting  ✅*"));

    }

    public void getCash(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Buyurtmangiz qabul qilindi ! Tez orada mutaxasislarimisz siz bilan boglanadi*"));
        orderClient.setCashOrOnline(Payment.NAQD);
        orderClient.setStatus(ProfileStatus.ACTIVE);
        saveUser(message.getChatId()).setStep(null);
        OrderClientEntity save = oderClientRepository.save(orderClient);
        orderClient = new OrderClientEntity();
        sendOrder(message, save);

    }

    public void sendOrder(Message message, OrderClientEntity save) {
        myTelegramBot.send(SendMsg.sendMsg(1024661550L,
                "        *>>>>>>>>>>>Buyurtma<<<<<<<<<<<* \n" +
                        "\n*Buyurtma ID : * " + save.getId() +
                        "" +
                        "\n*ISM VA FAMILIYA : * " + save.getFullName() + "" +
                        "\n*TELEFON RAQAM : * " + save.getPhone() + "" +
                        "\n*Buyurtma sanasi : * " + save.getOrderDate() + "" +
                        "\n*Status :* " + save.getStatus() + "" +
                        "\n*To'lov turi : * " + save.getCashOrOnline(),
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("zakasni tugatish ✅", "zakas")),
                        InlineButton.row(InlineButton.button("Mashina chiqadigan manzil \uD83D\uDCCD", "loc1")),
                        InlineButton.row(InlineButton.button("Mashina boradigan manzil \uD83D\uDCCD", "loc2"))))));


    }

    public boolean checkPhone(Message message) {
        if (!message.getText().startsWith("+998") || message.getText().length() != 13) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Iltimos telefon raqamni quyidagi ko'rinishda kiriting !*" +
                            "*\nMasalan : +998901234567  ✅*"));
            return false;
        }

        for (int i = 0; i < message.getText().length(); i++) {
            if (Character.isAlphabetic(message.getText().charAt(i))) {
                myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                        "*Iltimos telefon raqamni quyidagi ko'rinishda kiriting !*" +
                                "*\nMasalan : +998901234567  ✅*"));
                return false;
            }
        }

        return true;
    }
}
