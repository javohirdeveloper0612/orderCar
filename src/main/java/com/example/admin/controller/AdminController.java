package com.example.admin.controller;
import com.example.service.CallBackService;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Controller
public class AdminController {

    private final AdminMainController mainController;
    private final CallBackService callBackService;
    private final AdminMainController adminMainController;

    public AdminController(AdminMainController mainController,
                           CallBackService callBackService,
                           AdminMainController adminMainController) {

        this.mainController = mainController;
        this.callBackService = callBackService;
        this.adminMainController = adminMainController;
    }

    public void handle(Update update) {

        if (update.hasMessage()) {

            mainController.handle(update);

        }

        if(update.hasCallbackQuery()) {

            Message message = update.getCallbackQuery().getMessage();
            String query = update.getCallbackQuery().getData();

            String[] arr = query.split("/");

            if (arr.length < 2) {
                return;
            }

            if (arr[1].equals("year") || arr[1].equals("month")) {

                callBackService.sendCalendar(message, Integer.parseInt(arr[2]),
                        Integer.parseInt(arr[3]));

            } else if (arr[1].equals("day")) {

                LocalDate date = callBackService.getDate
                        (message, Integer.parseInt(arr[2]),
                                Integer.parseInt(arr[3]),
                                Integer.parseInt(arr[4]));

                adminMainController.getOrderDateAdmin(date, message);

            }

        }
    }
}
