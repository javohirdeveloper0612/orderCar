package com.example.controller;
import com.example.command.*;
import com.example.service.BotinstructionService;
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
    private final BotinstructionService botinstructionService;
    private final TransportUslugaController transportController;
    private final HelpCommand helpCommand;
    private final InstructionCommand instructionCommand;
    private final ContactCommand contactCommand;
    private final LocationCommand locationCommand;
    private final CreatedCommand createdCommand;

    @Lazy
    @Autowired
    public MainController(MainService mainService,
                          BotinstructionService botinstructionService,
                          TransportUslugaController transportController,
                          HelpCommand helpCommand,
                          InstructionCommand instructionCommand,
                          ContactCommand contactCommand,
                          LocationCommand locationCommand,
                          CreatedCommand createdCommand) {


        this.mainService = mainService;
        this.botinstructionService = botinstructionService;
        this.transportController = transportController;
        this.helpCommand = helpCommand;
        this.instructionCommand = instructionCommand;
        this.contactCommand = contactCommand;
        this.locationCommand = locationCommand;
        this.createdCommand = createdCommand;
    }

    public void handler(Message message) {

        if (message.hasText()) {

            var text = message.getText();
            var telegramUsers = saveUser(message.getChatId());

            if(text.equals("/start")){
                mainService.mainMenu(message.getChatId());
            }
            if(text.equals("/help")){
                helpCommand.helpCommand(message);
            }
            if(text.equals("/instruction")){
                instructionCommand.instructionCommand(message);
            }
            if(text.equals("/contact")){
                contactCommand.contactCommand(message);
            }
            if(text.equals("/location")){
                locationCommand.locationCommand(message);
            }
            if(text.equals("/created")){
                createdCommand.creativeTeamCommand(message);
            }

            if(text.equals(ButtonName.backMainMenuGlavniy)){
                mainService.mainMenu(message.getChatId());
                telegramUsers.setStep(Step.MAIN);
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
                    case ButtonName.botinstruction -> {
                        botinstructionService.botInstruction(message);
                    }

                    case ButtonName.setting -> {
                        mainService.setting(message);
                    }

                }

                //-> TransportController

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
