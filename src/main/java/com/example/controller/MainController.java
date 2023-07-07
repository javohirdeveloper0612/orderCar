package com.example.controller;
import com.example.util.ButtonName;
import com.example.util.Step;
import com.example.util.TelegramUsers;
import com.example.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    private final MainService mainService;
    private final List<TelegramUsers> usersList = new ArrayList<>();

    private final TransportUslugaController transportController;

    @Lazy
    @Autowired
    public MainController(MainService mainService, TransportUslugaController transportController) {
        this.mainService = mainService;
        this.transportController = transportController;
    }

    public void handler(Message message) {

        if (message.hasText()) {
            var text = message.getText();
            var telegramUsers = saveUser(message.getChatId());
            if (text.equals("/start")) {
                mainService.mainMenu(message);
                telegramUsers.setStep(Step.MAIN);
                transportController.saveUser(message.getChatId()).setStep(null);
                return;
            } else if (text.equals("/help")) {
                mainService.help(message);
                telegramUsers.setStep(Step.MAIN);
                return;
            }

            if (telegramUsers.getStep() == null) {
                telegramUsers.setStep(Step.MAIN);
            }

            if (telegramUsers.getStep().equals(Step.MAIN)) {
                switch (message.getText()) {
                    case ButtonName.transportusluga -> {
                        mainService.transportMenu(message);
                        telegramUsers.setStep(Step.TRANSPORT);
                    }
                    case ButtonName.metallBuyum -> {
                        //metalbuyumcontroller
                        mainService.metallBuyumMenu(message);
                    }
                    case ButtonName.metallprokat -> {
                        //metallprokatcontroller
                        mainService.metalProkatMenu(message);
                    }
                    case ButtonName.contact -> {
                        mainService.contact(message);
                    }
                    case ButtonName.location -> {
                        mainService.location(message);
                    }

                    case ButtonName.setting -> {
                        mainService.setting(message);
                    }

                }

            } else if (telegramUsers.getStep().equals(Step.TRANSPORT)) {
                transportController.handler(message);

            }
        } else if (message.hasContact()) {
            transportController.handler(message);
        } else if (message.hasLocation()) {
            transportController.handler(message);
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

}