package com.example.admin.controller;

import com.example.admin.button.ButtonName;
import com.example.admin.service.AdminService;
import com.example.entity.LocationClient;
import com.example.entity.OrderClientEntity;
import com.example.entity.ProfileEntity;
import com.example.enums.Payment;
import com.example.enums.ProfileRole;
import com.example.enums.Status;
import com.example.mytelegram.MyTelegramBot;
import com.example.repository.OrderClientRepository;
import com.example.repository.ProfileRepository;
import com.example.service.TransportUslugaService;
import com.example.util.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TextController {

    private final AdminService adminService;
    private ProfileEntity profile = new ProfileEntity();
    private OrderClientEntity client = new OrderClientEntity();
    private final List<TelegramUsers> list = new ArrayList<>();
    private final ProfileRepository profileRepository;
    private final TransportUslugaService uslugaService;
    private final OrderClientRepository orderClientRepository;

    private final MyTelegramBot myTelegramBot;

    @Lazy
    public TextController(AdminService adminService, ProfileRepository profileRepository, TransportUslugaService uslugaService, OrderClientRepository orderClientRepository, MyTelegramBot myTelegramBot) {
        this.adminService = adminService;
        this.profileRepository = profileRepository;
        this.uslugaService = uslugaService;
        this.orderClientRepository = orderClientRepository;
        this.myTelegramBot = myTelegramBot;
    }

    public void handle(Update update) {

        Message message = update.getMessage();
        TelegramUsers step = saveUser(message.getChatId());

        if (message.hasText()) {

            if (message.getText().equals("/start")) {
                adminService.mainMenu(message);
                step.setStep(Step.MAIN);
            }

            if (step.getStep().equals(Step.MAIN)) {

                switch (message.getText()) {

                    case ButtonName.onlineOrder -> {
                        adminService.onlineOrder(message);
                        step.setStep(Step.GETPHONEOFFLINE);
                    }
                    case ButtonName.activeOrder -> adminService.activeOrder(message);
                    case ButtonName.notactiveOrder -> adminService.notActiveOrder(message);
                    case ButtonName.onlineProfit -> adminService.onlineProfit(message);
                    case ButtonName.setting -> {
                        adminService.setting(message);
                        step.setStep(Step.SETTING);

                    }
                }

            } else if (step.getStep().equals(Step.GETPHONEOFFLINE)) {

                if (adminService.checkPhone(message)) {
                    client.setPhone(message.getText());
                    adminService.getFullNameOffline(message);
                    step.setStep(Step.GETFULLNAMEOFFLINE);
                }

            } else if (step.getStep().equals(Step.GETFULLNAMEOFFLINE)) {

                client.setFullName(message.getText());
                uslugaService.replyStart(message.getChatId());
            }


            if (step.getStep().equals(Step.SETTING)) {

                switch (message.getText()) {

                    case ButtonName.addDriver -> {
                        adminService.addDriver(message);
                        step.setStep(Step.GETFULLNAME);
                    }
                    case ButtonName.deleteDriver -> {
                        adminService.deleteDriver(message);
                        step.setStep(Step.DELETEDRIVER);
                    }
                    case ButtonName.listOfDriver -> {
                        adminService.listOfDriver(message);
                    }
                    case ButtonName.back -> {
                        adminService.mainMenu(message);
                        step.setStep(Step.MAIN);
                    }

                }

            } else if (step.getStep().equals(Step.GETFULLNAME)) {

                profile.setFullName(message.getText());
                adminService.getDriverPhone(message);
                step.setStep(Step.GETPHONE);

            } else if (step.getStep().equals(Step.GETPHONE)) {

                if (adminService.checkPhone(message)) {
                    profile.setPhone(message.getText());
                    profile.setRole(ProfileRole.DRIVER);
                    adminService.claimMessage(message);
                    profileRepository.save(profile);
                    profile = new ProfileEntity();
                    adminService.mainMenu(message);
                    step.setStep(Step.MAIN);
                }

            } else if (step.getStep().equals(Step.DELETEDRIVER)) {

                if (adminService.checkPhone(message)) {
                    if (adminService.claimDeleting(message)) {
                        adminService.setting(message);
                        step.setStep(Step.SETTING);
                    } else {
                        adminService.setting(message);
                        step.setStep(Step.SETTING);
                    }
                }

            } else if (step.getStep().equals(Step.GETFROMWHERELOCATION)) {

                getFromWhereLocation(message);
            } else if (step.getStep().equals(Step.GETTOWHERELOCATION)) {

                getToWhereLocation(message);
            }

        } else if (message.hasLocation()) {

            if (step.getStep().equals(Step.GETTOWHERELOCATION)) {
                client.setToWhere(getCurrentLocation(message));
                client.setPayment(Payment.НАЛИЧНЫЕ);
                client.setStatus(Status.ACTIVE);
                orderClientRepository.save(client);
                sendOrder();
                client = new OrderClientEntity();
                adminService.claimMessage(message);

                adminService.mainMenu(message);
                step.setStep(null);

            } else {

                client.setFromWhere(getCurrentLocation(message));
                getToWhereLocation(message);
                saveUser(message.getChatId()).setStep(Step.GETTOWHERELOCATION);
            }
        }
    }

    private LocationClient getCurrentLocation(Message message) {
        Double latitude = message.getLocation().getLatitude();
        Double longitude = message.getLocation().getLongitude();
        LocationClient locationClient = new LocationClient();
        locationClient.setLongitude(longitude);
        locationClient.setLatitude(latitude);
        return locationClient;
    }

    private void getToWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Куда едет машина? Пожалуйста, поделитесь местоположением!*"));
    }

    public TelegramUsers saveUser(Long chatId) {
        for (TelegramUsers users : list) {
            if (users.getChatId().equals(chatId)) {
                return users;
            }
        }
        var users = new TelegramUsers();
        users.setChatId(chatId);
        list.add(users);
        return users;
    }

    public void getOrderDate(LocalDate date, Message message) {
        Boolean exists = orderClientRepository.existsByOrderDateAndStatus(date,Status.ACTIVE);
        if (exists) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Извините, эта дата забронирована. Выберите другую дату!*"));
            uslugaService.replyStart(message.getChatId());

        } else {
            getFromWhereLocation(message);
            saveUser(message.getChatId()).setStep(Step.GETFROMWHERELOCATION);
            client.setOrderDate(date);
        }
    }

    public void getFromWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(), "*Откуда выезжает машина? Пожалуйста, поделитесь местоположением!*"));

    }

    public void sendOrder() {
        var list = profileRepository.findAllByRole(ProfileRole.DRIVER);
        if (list.isEmpty()) {
            return;
        }
        for (ProfileEntity entity : list) {
            order(entity);
            sms(entity);
        }

    }

    public void order(ProfileEntity entity) {
        myTelegramBot.send(SendMsg.sendMsg(entity.getChatId(),
                "        *>>>>>>>>>>>Buyurtma<<<<<<<<<<<* \n" +
                        "\n*Buyurtma ID : * " + client.getId() +
                        "\n*ISM VA FAMILIYA : * " + client.getFullName() +
                        "\n*TELEFON RAQAM : * " + client.getPhone() +
                        "\n*Buyurtma sanasi : * " + client.getOrderDate() +
                        "\n*Status :* " + client.getStatus() +
                        "\n*To'lov turi : * " + client.getPayment(),
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("Прием заказа ✅", "accept_order#" + client.getId()))))));

    }

    public void sms(ProfileEntity entity) {
        SmsServiceUtil.sendSmsOrder(SmsServiceUtil.removePlusSign(entity.getPhone()),
                ">>>>>>>>>>>Buyurtma<<<<<<<<<<<\n" +
                        "\nBuyurtma ID : " + client.getId() +
                        "" +
                        "\nISM VA FAMILIYA :  " + client.getFullName() + "" +
                        "\nTELEFON RAQAM : " + client.getPhone() + "" +
                        "\nBuyurtma sanasi : " + client.getOrderDate() + "" +
                        "\nStatus : " + client.getStatus() + "" +
                        "\nTo'lov turi : " + client.getPayment());
    }
}
