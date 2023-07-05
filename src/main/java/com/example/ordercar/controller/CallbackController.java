package com.example.ordercar.controller;


import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.service.CallBackService;
import com.example.ordercar.driver.service.DriverService;
import com.example.ordercar.service.MainService;
import com.example.ordercar.util.SendMsg;
import com.example.ordercar.util.Step;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Controller
public class CallbackController {

    private final MyTelegramBot myTelegramBot;
    private final CallBackService callBackService;
    private final TransportUslugaController uslugaController;
    private final MainService mainService;
    private final MainController mainController;

    private final DriverService driverService;


    @Lazy
    public CallbackController(MyTelegramBot myTelegramBot,
                              CallBackService callBackService,
                              TransportUslugaController uslugaController, MainService mainService, MainController mainController, DriverService driverService) {
        this.myTelegramBot = myTelegramBot;

        this.callBackService = callBackService;
        this.uslugaController = uslugaController;

        this.mainService = mainService;
        this.mainController = mainController;
        this.driverService = driverService;
    }

    public void handler(Update update) {

        Message message = update.getCallbackQuery().getMessage();
        String query = update.getCallbackQuery().getData();

        String[] parts = query.split("#");
        long locationId = 0;
        if (parts.length == 2) {
           locationId = Long.parseLong(parts[1]);
        }

        switch (parts[0]) {
            case "view_loc" -> myTelegramBot.send(SendMsg.sendLocation(message.getChatId(), message.getMessageId()));

            case "finish_order" -> driverService.finishOrder(message,locationId,message.getMessageId());

            case "loc1", "loc2" -> driverService.getLocation(message, parts,message.getMessageId());

            case "accept_order" -> driverService.acceptOrder(message, locationId);

            case "back" -> {
                myTelegramBot.send(SendMsg.deleteMessage(message.getChatId(), message.getMessageId()));
                mainService.mainMenu(message);
                mainController.saveUser(message.getChatId()).setStep(Step.MAIN);
            }
            case "payme" -> {
                callBackService.getPayMe(message);
                uslugaController.saveUser(message.getChatId()).setStep(Step.PAYMENT);

            }
            case "click" -> {
                myTelegramBot.send(SendMsg.deleteMessage(message.getChatId(), message.getMessageId()));
                callBackService.getClick(message);
                mainService.mainMenu(message);
                mainController.saveUser(message.getChatId()).setStep(Step.MAIN);

            }
            case "humo" -> {
                myTelegramBot.send(SendMsg.deleteMessage(message.getChatId(), message.getMessageId()));
                callBackService.getHumo(message);
                mainService.mainMenu(message);
                mainController.saveUser(message.getChatId()).setStep(Step.MAIN);

            }
            case "uzum" -> {
                myTelegramBot.send(SendMsg.deleteMessage(message.getChatId(), message.getMessageId()));
                callBackService.getUzum(message);
                mainService.mainMenu(message);
                mainController.saveUser(message.getChatId()).setStep(Step.MAIN);

            }
            case "naqd" -> {
                myTelegramBot.send(SendMsg.deleteMessage(message.getChatId(), message.getMessageId()));
                uslugaController.getCash(message);
                mainService.mainMenu(message);
                mainController.saveUser(message.getChatId()).setStep(Step.MAIN);
            }

        }

        String[] arr = query.split("/");
        if (arr.length < 2) {
            return;
        }

        if (arr[1].equals("year") || arr[1].equals("month")) {
            callBackService.sendCalendar(message, Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
        } else if (arr[1].equals("day")) {
            LocalDate date = callBackService.getDate(message, Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), Integer.parseInt(arr[4]));
            uslugaController.getOrderDate(date, message);
        }
    }


}

