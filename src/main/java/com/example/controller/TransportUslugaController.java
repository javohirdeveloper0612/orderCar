package com.example.controller;
import com.example.entity.LocationClient;
import com.example.entity.OrderClientEntity;
import com.example.entity.ProfileEntity;
import com.example.enums.Payment;
import com.example.enums.ProfileRole;
import com.example.enums.Status;
import com.example.mytelegram.MyTelegramBot;
import com.example.repository.OrderClientRepository;
import com.example.repository.ProfileRepository;
import com.example.service.MainService;
import com.example.service.OrderClientService;
import com.example.service.TransportUslugaService;
import com.example.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TransportUslugaController {

    public static final List<TelegramUsers> usersList = new ArrayList<>();
    private final MainService mainService;
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
                            myTelegramBot.send(SendMsg.sendPhotoCarInfo(message.getChatId(), "Характеристики  ✅"));
                            transportService.guideOrderCar(message);
                            transportService.replyStart(message.getChatId());

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

                    if (transportService.checkPhone(message)) {
                        sendSmsCode(message);
                        transportStep.setStep(Step.CHECKSMS);
                    }
                }
                case CHECKSMS -> {
                    if (checkSmsCode(message)) {
                        transportService.getFullName(message.getChatId());
                        transportStep.setStep(Step.GETFULLNAME);
                    } else {
                        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                                "*Код подтверждения неверен. Пожалуйста, введите повторно*"));
                    }
                }
                case GETFULLNAME -> {

                    Optional<OrderClientEntity> optional = orderClientRepository.findTop1ByChatIdAndStatusAndIsVisibleTrueAndPhoneIsNotNullOrderByOrderDateDesc(message.getChatId(), Status.NOTACTIVE);

                    if (optional.isEmpty()) {
                        return;
                    }

                    OrderClientEntity orderClient = optional.get();
                    orderClient.setFullName(message.getText());
                    orderClientRepository.save(orderClient);
                    transportService.getPayment(message.getChatId(), orderClient.getId());

                }

                case GETTOWHERELOCATION -> transportService.getToWhereLocation(message);
                case GETFROMWHERELOCATION -> transportService.getFromWhereLocation(message);

            }

        } else if (message.hasContact()) {

            transportService.sendContact(message);
            transportStep.setStep(Step.CHECKSMS);

        } else if (message.hasLocation()) {

            if (transportStep.getStep().equals(Step.GETTOWHERELOCATION)) {

                Optional<OrderClientEntity> optional = orderClientRepository.findTop1ByChatIdAndStatusAndIsVisibleTrueOrderByOrderDateDesc(message.getChatId(), Status.NOTACTIVE);
                if (optional.isEmpty()) {
                    return;
                }
                OrderClientEntity orderClient = optional.get();

                if (orderClient.getFromWhere() == null) {
                    return;
                }
                orderClient.setToWhere(getCurrentLocation(message));
                double km = KmUtil.calculateDistance(orderClient.getFromWhere(), orderClient.getToWhere());
                long amount = KmUtil.calculateSum(km);
                orderClient.setAmount(amount);
                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                        "Расстояние от начальной точки автомобиля до пункта назначения автомобиля: " + km + " km"));
                myTelegramBot.send(SendMsg.sendPhoto(message.getChatId(),
                        "Сумма расстояний от места отправления до места доставки: " + amount / 100 + "." + amount % 100 + " so'm"));

                orderClientRepository.save(orderClient);
//                getPayment(message, orderClient.getId());
                transportService.orderCar(message.getChatId());
//                transportStep.setStep(Step.PAYMENT);
            } else {
                Optional<OrderClientEntity> optional = orderClientRepository.findTop1ByChatIdAndStatusAndIsVisibleTrueOrderByOrderDateDesc(message.getChatId(), Status.NOTACTIVE);
                if (optional.isEmpty()) {
                    return;
                }
                OrderClientEntity orderClient = optional.get();
                orderClient.setFromWhere(getCurrentLocation(message));
                orderClientRepository.save(orderClient);
                transportService.getToWhereLocation(message);
                saveUser(message.getChatId()).setStep(Step.GETTOWHERELOCATION);
            }
        }
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
        Optional<OrderClientEntity> optional = orderClientRepository.findTop1ByChatIdAndStatusAndIsVisibleTrueAndPhoneIsNotNullOrderByOrderDateDesc(message.getChatId(), Status.NOTACTIVE);
        if (optional.isEmpty()) {
            return false;
        }

        return message.getText().equals(optional.get().getSmsCode());
    }

    public void getOrderDate(LocalDate localDate, Message message) {

        Boolean exists = orderClientRepository.existsByOrderDateAndStatus(localDate, Status.ACTIVE);
        if (exists) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Извините, эта дата забронирована. Выберите другую дату !*"));
            transportService.replyStart(message.getChatId());

        } else {

            Optional<OrderClientEntity> optional =
                    orderClientRepository.findTop1ByChatIdAndStatusAndIsVisibleTrueOrderByOrderDateDesc
                            (message.getChatId(), Status.NOTACTIVE);

            if (optional.isPresent()) {
                OrderClientEntity orderClient = optional.get();
                orderClient.setVisible(false);
                orderClientRepository.save(orderClient);
            }

            OrderClientEntity orderClient = new OrderClientEntity();
            transportService.getFromWhereLocation(message);
            saveUser(message.getChatId()).setStep(Step.GETFROMWHERELOCATION);
            orderClient.setOrderDate(localDate);
            orderClient.setChatId(message.getChatId());
            orderClientRepository.save(orderClient);
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
        Optional<OrderClientEntity> optional = orderClientRepository.findTop1ByChatIdAndStatusAndIsVisibleTrueOrderByOrderDateDesc(message.getChatId(), Status.NOTACTIVE);
        if (optional.isEmpty()) {
            return;
        }

        OrderClientEntity orderClient = optional.get();
        orderClient.setSmsCode(randomNumber);
        orderClient.setPhone(phone);
        orderClientRepository.save(orderClient);
        SmsServiceUtil.sendSmsCode(SmsServiceUtil.removePlusSign(phone), randomNumber);
        SendMessage sendMessage = SendMsg.sendMsgParse(message.getChatId(),
                "*" + phone + "*" + "**" +
                        "*\n- Пожалуйста, введите проверочный код  ✅*");
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardRemove);
        myTelegramBot.send(sendMessage);
    }


    public void acceptOrder(Long chatId, Long orderId, Payment payment) {
        Optional<OrderClientEntity> optional = orderClientRepository.findById(orderId);
        if (optional.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(chatId,
                    "Ошибка при заказе, попробуйте еще раз  \uD83D\uDD04"));
            return;
        }
        OrderClientEntity orderClient = optional.get();
        orderClient.setStatus(Status.ACTIVE);
        orderClient.setPayment(payment);


        myTelegramBot.send(SendMsg.sendMsg(chatId,
                "*Ваш заказ принят, наши специалисты свяжутся с вами в ближайшее время  ✅*"));

        orderClientRepository.save(orderClient);
        sendOrder(orderClient);
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
                        InlineButton.row(InlineButton.button("Прием заказа ✅",
                                "accept_order#" + orderClient.getId()))))));
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


}
