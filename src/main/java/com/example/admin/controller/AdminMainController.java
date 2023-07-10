package com.example.admin.controller;
import com.example.admin.service.*;
import com.example.admin.util.ButtonAdmin;
import com.example.admin.util.ButtonNameAdmin;
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
public class AdminMainController {

    private final MainServiceAdmin mainServiceAdmin;
    private ProfileEntity profile = new ProfileEntity();
    private OrderClientEntity client = new OrderClientEntity();
    private final List<TelegramUsers> list = new ArrayList<>();
    private final ProfileRepository profileRepository;
    private final TransportUslugaService uslugaService;
    private final OrderClientRepository orderClientRepository;
    private final MyTelegramBot myTelegramBot;
    private final ButtonAdmin buttonAdmin;
    private final DriverServiceAdmin driverServiceAdmin;
    private final OrderPhoneService orderPhoneService;
    private final DeleteOrderService deleteOrderService;
    private final UpdateDayService updateDayService;

    @Lazy
    public AdminMainController(MainServiceAdmin adminService,
                               ProfileRepository profileRepository,
                               TransportUslugaService uslugaService,
                               OrderClientRepository orderClientRepository,
                               MyTelegramBot myTelegramBot,
                               ButtonAdmin buttonAdmin,
                               DriverServiceAdmin driverServiceAdmin,
                               OrderPhoneService orderPhoneService,
                               DeleteOrderService deleteOrderService,
                               UpdateDayService updateDayService) {

        this.mainServiceAdmin = adminService;
        this.profileRepository = profileRepository;
        this.uslugaService = uslugaService;
        this.orderClientRepository = orderClientRepository;
        this.myTelegramBot = myTelegramBot;
        this.buttonAdmin = buttonAdmin;
        this.driverServiceAdmin = driverServiceAdmin;
        this.orderPhoneService = orderPhoneService;
        this.deleteOrderService = deleteOrderService;
        this.updateDayService = updateDayService;
    }

    public void handle(Update update) {

        Message message = update.getMessage();

        TelegramUsers step = saveUser(message.getChatId());

        if (message.hasText()) {

            if (message.getText().equals("/start")) {
                buttonAdmin.mainMenu(message);
                step.setStep(Step.MAIN);
            }

            if (step.getStep().equals(Step.MAIN)) {

                switch (message.getText()) {

                    case ButtonNameAdmin.onlineOrder -> {
                        orderPhoneService.onlineOrder(message);
                        step.setStep(Step.GETPHONEOFFLINE);
                    }
                    case ButtonNameAdmin.activeOrder -> {
                        mainServiceAdmin.activeOrder(message);
                    }
                    case ButtonNameAdmin.notactiveOrder -> {
                        mainServiceAdmin.notActiveOrder(message);
                    }
                    case ButtonNameAdmin.deleteOrder -> {

                        deleteOrderService.deleteOrderList(message);
                        step.setStep(Step.DELETEORDERLIST);

                    }
                    case ButtonNameAdmin.updateDayOrder -> {
                        updateDayService.replyStartUpdateOrder(message.getChatId());
                    }
                    case ButtonNameAdmin.setting -> {
                        mainServiceAdmin.setting(message);
                        step.setStep(Step.SETTING);
                    }
                }


            } else if (step.getStep().equals(Step.GETPHONEOFFLINE)) {

                if (mainServiceAdmin.checkPhone(message)) {
                    client.setPhone(message.getText());
                    orderPhoneService.getFullNameOffline(message);
                    step.setStep(Step.GETFULLNAMEOFFLINE);
                }

            } else if (step.getStep().equals(Step.GETFULLNAMEOFFLINE)) {

                client.setFullName(message.getText());
                orderPhoneService.replyStartAdmin(message.getChatId());

            }

            if (step.getStep().equals(Step.GETMONEYPHONE)) {

                Long amount = Long.valueOf(message.getText());
                client.setAmount(amount);
                step.setStep(Step.SAVEORDERPHONE);

            }

            if (step.getStep().equals(Step.SAVEORDERPHONE)) {

                client.setPayment(Payment.NAQD);
                client.setStatus(Status.ACTIVE);
                orderClientRepository.save(client);
                sendOrder();
                orderPhoneService.saveOrderPhone(message);
                client = new OrderClientEntity();
                step.setStep(Step.SAVEOKPHONEORDER);

            }

            if(step.getStep().equals(Step.SAVEOKPHONEORDER)){

                buttonAdmin.mainMenu(message);
                step.setStep(Step.MAIN);

            }

            if (step.getStep().equals(Step.SETTING)) {

                switch (message.getText()) {

                    case ButtonNameAdmin.addDriver -> {
                        driverServiceAdmin.addDriver(message);
                        step.setStep(Step.GETFULLNAME);
                    }
                    case ButtonNameAdmin.deleteDriver -> {
                        driverServiceAdmin.deleteDriver(message);
                        step.setStep(Step.DELETEDRIVER);
                    }
                    case ButtonNameAdmin.listOfDriver -> {
                        driverServiceAdmin.listOfDriver(message);
                    }
                    case ButtonNameAdmin.back -> {
                        buttonAdmin.mainMenu(message);
                        step.setStep(Step.MAIN);
                    }
                }

            } else if (step.getStep().equals(Step.GETFULLNAME)) {

                profile.setFullName(message.getText());
                driverServiceAdmin.getDriverPhone(message);
                step.setStep(Step.GETPHONE);

            } else if (step.getStep().equals(Step.GETPHONE)) {

                if (mainServiceAdmin.checkPhone(message)) {

                    profile.setPhone(message.getText());
                    profile.setRole(ProfileRole.DRIVER);
                    mainServiceAdmin.claimMessage(message);
                    profileRepository.save(profile);
                    profile = new ProfileEntity();
                    buttonAdmin.mainMenu(message);
                    step.setStep(Step.MAIN);

                }

            } else if (step.getStep().equals(Step.DELETEDRIVER)) {

                if (mainServiceAdmin.checkPhone(message)) {

                    if (mainServiceAdmin.claimDeleting(message)) {
                        mainServiceAdmin.setting(message);
                        step.setStep(Step.SETTING);

                    } else {

                        mainServiceAdmin.setting(message);
                        step.setStep(Step.SETTING);

                    }
                }
            }
        }
        

        if (step.getStep().equals(Step.DELETEORDERLIST)) {

            deleteOrderService.deleteOrderGetId(message);
            step.setStep(Step.DELETEORDERBYID);

        }

        if (step.getStep().equals(Step.DELETEORDERBYID)) {

            deleteOrderService.deleteOrderById(message);
            step.setStep(Step.DELETEORDER);

        }

        if (step.getStep().equals(Step.DELETEORDER)) {

            deleteOrderService.deleteOrder(message);
            step.setStep(Step.DELETEORDERBACKMENU);

        }

        if (step.getStep().equals(Step.DELETEORDERBACKMENU)) {

            buttonAdmin.mainMenu(message);
            step.setStep(Step.MAIN);

        }

    }

    public void getOrderDateAdmin(LocalDate date, Message message) {

        Boolean exists = orderClientRepository.existsByOrderDateAndStatus(date, Status.ACTIVE);

        if (exists) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*- Извините, эта дата забронирована. Выберите другую дату!*"));
            orderPhoneService.replyStartAdmin(message.getChatId());

        } else {

            client.setOrderDate(date);
            orderPhoneService.guiderOrderPhoneMoney(message);
            orderPhoneService.getMoney(message);
            saveUser(message.getChatId()).setStep(Step.GETMONEYPHONE);

        }
    }

    public TelegramUsers saveUser(Long chatId) {
        for (TelegramUsers adminUsers : list) {
            if (adminUsers.getChatId().equals(chatId)) {
                return adminUsers;
            }
        }
        var users = new TelegramUsers();
        users.setChatId(chatId);
        list.add(users);
        return users;
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
                "        *>>>>>>>>>>> Buyurtma <<<<<<<<<<<* \n" +
                        "\n*Buyurtma ID : * " + client.getId() +
                        "" +
                        "\n*ISM VA FAMILIYA : * " + client.getFullName() + "" +
                        "\n*TELEFON RAQAM : * " + client.getPhone() + "" +
                        "\n*Buyurtma sanasi : * " + client.getOrderDate() + "" +
                        "\n*Status :* " + client.getStatus() + "" +
                        "\n*To'lov turi : * " + client.getPayment(),
                InlineButton.keyboardMarkup(InlineButton.rowList(
                        InlineButton.row(InlineButton.button("Прием заказа ✅",
                                "accept_order#" + client.getId()))))));

    }

    public void sms(ProfileEntity entity) {
        SmsServiceUtil.sendSmsOrder(SmsServiceUtil.removePlusSign(entity.getPhone()),
                ">>>>>>>>>>> Buyurtma <<<<<<<<<<<\n" +
                        "\nBuyurtma ID : " + client.getId() +
                        "" +
                        "\nISM VA FAMILIYA :  " + client.getFullName() + "" +
                        "\nTELEFON RAQAM : " + client.getPhone() + "" +
                        "\nBuyurtma sanasi : " + client.getOrderDate() + "" +
                        "\nStatus : " + client.getStatus() + "" +
                        "\nTo'lov turi : " + client.getPayment());
    }
}
