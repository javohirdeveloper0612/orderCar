package com.example.ordercar.admin.controller;

import com.example.ordercar.admin.button.ButtonName;
import com.example.ordercar.admin.service.AdminService;
import com.example.ordercar.entity.LocationClient;
import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.entity.ProfileEntity;
import com.example.ordercar.enums.Payment;
import com.example.ordercar.enums.ProfileRole;
import com.example.ordercar.enums.Status;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.repository.OrderClientRepository;
import com.example.ordercar.repository.ProfileRepository;
import com.example.ordercar.service.TransportUslugaService;
import com.example.ordercar.util.SendMsg;
import com.example.ordercar.util.Step;
import com.example.ordercar.util.TelegramUsers;
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

            if (step.getStep() == null) {
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
                client.setPayment(Payment.NAQD);
                client.setStatus(Status.ACTIVE);
                orderClientRepository.save(client);
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
                "*Mashina qayerga boradi ? Iltimos locatsiya ulashing !*"));
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
        Boolean exists = orderClientRepository.existsByOrderDate(date);
        if (exists) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Kechirasiz, ushbu  sana band qilingan. Boshqa sanani tanlang!*"));
            uslugaService.replyStart(message.getChatId());

        } else {
            getFromWhereLocation(message);
            saveUser(message.getChatId()).setStep(Step.GETFROMWHERELOCATION);
            client.setOrderDate(date);
        }
    }

    public void getFromWhereLocation(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(), "*Mashina qayerdan yo'lga chiqadi ? Iltimos locatsiya ulashing !*"));

    }
}
