package com.example.ordercar.controller;
import com.example.ordercar.entity.LocationClient;
import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.entity.ProfileEntity;
import com.example.ordercar.enums.Payment;
import com.example.ordercar.enums.ProfileRole;
import com.example.ordercar.enums.Status;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.repository.OrderClientRepository;
import com.example.ordercar.repository.ProfileRepository;
import com.example.ordercar.service.MainService;
import com.example.ordercar.service.OrderClientService;
import com.example.ordercar.service.TransportUslugaService;
import com.example.ordercar.util.*;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.InlineButton;
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
    private final OrderClientRepository orderClientRepository;
    private final MyTelegramBot myTelegramBot;
    private final ProfileRepository profileRepository;


    @Autowired
    public TransportUslugaController(MainService mainService,
                                     MainController mainController,
                                     TransportUslugaService transportService,
                                     OrderClientService orderClientService,
                                     OrderClientRepository orderClientRepository,
                                     MyTelegramBot myTelegramBot, ProfileRepository profileRepository) {
        this.mainService = mainService;
        this.mainController = mainController;
        this.transportService = transportService;
        this.orderClientService = orderClientService;
        this.orderClientRepository = orderClientRepository;
        this.myTelegramBot = myTelegramBot;
        this.profileRepository = profileRepository;
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
                        case ButtonName.dataDriver -> mainService.dataVoditel(message);
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
                                "*Код подтверждения неверен. Пожалуйста, введите повторно*"));
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
                        "\uD83D\uDD22 *Введите сумму платежа минимум 1000: (UZS)*"));
                return false;
            }
        }

        return true;
    }

    private void getToWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Куда едет машина? Пожалуйста, поделитесь местоположением!*"));
    }

    private void getPayment(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Пожалуйста, выберите удобную для вас платежную систему*",
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  PAYME (Автоплатеж)", "payme")),
                        InlineButton.row(InlineButton.button("\uD83D\uDFE2  НАЛИЧНЫЕ ", "naqd")),
                        InlineButton.row(InlineButton.button("⬅️ НАЗАД", "back"))
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
        Boolean exists = orderClientRepository.existsByOrderDate(localDate);
        if (exists) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Извините, эта дата забронирована. Выберите другую дату!*"));
            transportService.replyStart(message.getChatId());

        } else {
            getFromWhereLocation(message);
            saveUser(message.getChatId()).setStep(Step.GETFROMWHERELOCATION);
            orderClient.setOrderDate(localDate);
        }
    }

    public void getFromWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(), "*Откуда выезжает машина? Пожалуйста, поделитесь местоположением!*"));

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
                "*" + phone + "*" + "* Код подтверждения был отправлен на этот номер!*" +
                        "*\nПожалуйста, введите проверочный код  ✅*"));
    }


    public void sendContact(Message message) {

        String randomNumber = RandomUtil.getRandomNumber();
        String phone = message.getContact().getPhoneNumber();
        orderClient.setSmsCode(randomNumber);
        orderClient.setPhone(phone);
        orderClient.setChatId(message.getChatId());
        SmsServiceUtil.sendSmsCode(SmsServiceUtil.removePlusSign(phone), randomNumber);
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*" + phone + "*" + "*Код подтверждения был отправлен на этот номер! *" +
                        "*\nПожалуйста, введите проверочный код  ✅*"));

    }

    public void getCash(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Ваш заказ принят! Наши специалисты свяжутся с вами в ближайшее время*"));
        orderClient.setPayment(Payment.NAQD);
        orderClient.setStatus(Status.ACTIVE);
        saveUser(message.getChatId()).setStep(null);
        OrderClientEntity save = orderClientRepository.save(orderClient);
        orderClient = new OrderClientEntity();
        sendOrder(save);
    }

    public void sendOrder(OrderClientEntity save) {

        var list = profileRepository.findAllByRole(ProfileRole.DRIVER);
        if (list.isEmpty()) {
            return;
        }
        for (ProfileEntity entity : list) {
            sendDataToDriver(entity, save);
            sendSms(save, entity);
        }
    }

    public void sendDataToDriver(ProfileEntity entity, OrderClientEntity orderClient) {
        myTelegramBot.send(SendMsg.sendMsg(entity.getChatId(),
                "        *>>>>>>>>>>>Заказ<<<<<<<<<<<* \n" +
                        "\n*ID заказа : * " + orderClient.getId() +
                        "" +
                        "\n*Имя и фамилия : * " + orderClient.getFullName() + "" +
                        "\n*Номер телефона : * " + orderClient.getPhone() + "" +
                        "\n*Дата заказа : * " + orderClient.getOrderDate() + "" +
                        "\n*Статус :* " + orderClient.getStatus() + "" +
                        "\n*Тип оплаты : * " + orderClient.getPayment(),
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("Прием заказа ✅", "accept_order#" + orderClient.getId()))))));
    }


    public void sendSms(OrderClientEntity orderClient, ProfileEntity entity) {
        SmsServiceUtil.sendSmsOrder(SmsServiceUtil.removePlusSign(entity.getPhone()),
                ">>>>>>>>>>>Заказ<<<<<<<<<<<\n" +
                        "\nИмя и фамилия : " + orderClient.getFullName() + " " +
                        "\nНомер телефона : " + orderClient.getPhone() + " " +
                        "\nДата заказа : " + orderClient.getOrderDate() + " " +
                        "\nСтатус :" + orderClient.getStatus() + " " +
                        "\nТип оплаты  : " + orderClient.getPayment());
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
}
