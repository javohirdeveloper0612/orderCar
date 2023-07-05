package com.example.ordercar.admin.controller;

import com.example.ordercar.service.CallBackService;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

@Controller
public class AdminController {

    private final TextController textController;
    private final CallBackService callBackService;

    public AdminController(TextController textController, CallBackService callBackService) {
        this.textController = textController;
        this.callBackService = callBackService;
    }


    public void handle(Update update) {

        if (update.hasMessage()) {
            textController.handle(update);
        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            String query = update.getCallbackQuery().getData();
            String[] arr = query.split("/");
            if (arr.length < 2) {
                return;
            }

            if (arr[1].equals("year") || arr[1].equals("month")) {
                callBackService.sendCalendar(message, Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
            } else if (arr[1].equals("day")) {
                LocalDate date = callBackService.getDate(message, Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), Integer.parseInt(arr[4]));
                textController.getOrderDate(date, message);
            }
        }
    }
}
