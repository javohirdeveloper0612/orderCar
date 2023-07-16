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


            if (text.equals("/start")) {
                mainService.mainMenu(message.getChatId());
                telegramUsers.setStep(Step.MAIN);
            }
            if (text.equals("/help")) {
                helpCommand.helpCommand(message);
                telegramUsers.setStep(Step.HELPCOMMAND);
            }
            if (text.equals("/instruction")) {
                instructionCommand.instructionCommand(message);
                telegramUsers.setStep(Step.INSTRUCTIONCOMMAND);
            }
            if (text.equals("/contact")) {
                contactCommand.contactCommand(message);
                telegramUsers.setStep(Step.CONTACTCOMMAND);
            }
            if (text.equals("/location")) {
                locationCommand.locationCommand(message);
                telegramUsers.setStep(Step.LOCATIONCOMMAND);
            }
            if (text.equals("/created")) {
                createdCommand.creativeTeamCommand(message);
                telegramUsers.setStep(Step.CREATEDCOMMAND);
            }


            if (telegramUsers.getStep().equals(Step.MAIN)) {

                switch (message.getText()) {

                    case ButtonName.transportusluga -> {
                        mainService.transportMenu(message);
                        telegramUsers.setStep(Step.TRANSPORT);
                    }
                    case ButtonName.metallBuyum -> {
                        //-> metalbuyumcontroller
                        mainService.metallBuyumMenu(message);
                        telegramUsers.setStep(Step.METALBUYUM);
                    }
                    case ButtonName.metallprokat -> {
                        //-> metallprokatcontroller
                        mainService.metalProkatMenu(message);
                        telegramUsers.setStep(Step.METALLPROKAT);
                    }
                    case ButtonName.contact -> {
                        mainService.contact(message);
                        telegramUsers.setStep(Step.CONTACT);
                    }
                    case ButtonName.location -> {
                        mainService.location(message);
                        telegramUsers.setStep(Step.LOCATION);
                    }
                    case ButtonName.botinstruction -> {
                        botinstructionService.botInstruction(message);
                        telegramUsers.setStep(Step.BOTINSTRUCTION);
                    }

                    case ButtonName.setting -> {
                        mainService.setting(message);
                        telegramUsers.setStep(Step.SETTINGS);
                    }

                }
            }

            if (text.equals(ButtonName.backMainMenu)) {
                mainService.mainMenu(message.getChatId());
                telegramUsers.setStep(Step.MAIN);
            }


            if (telegramUsers.getStep().equals(Step.TRANSPORT)) {
                transportController.handler(message);
            }


            if (message.hasContact()) {

                transportController.handler(message);

            }

            if (message.hasLocation()) {

                transportController.handler(message);

            }

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
