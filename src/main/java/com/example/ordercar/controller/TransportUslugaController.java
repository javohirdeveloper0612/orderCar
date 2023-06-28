package com.example.ordercar.controller;
import com.example.ordercar.service.MainService;
import com.example.ordercar.service.TransportUslugaService;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.Step;
import com.example.ordercar.util.TelegramUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TransportUslugaController {

    private final List<TelegramUsers> usersList = new ArrayList<>();

    private final MainService mainService;

    private final MainController mainController;

    private final TransportUslugaService transportService;

    @Autowired
    public TransportUslugaController(MainService mainService, MainController mainController, TransportUslugaService transportService) {
        this.mainService = mainService;
        this.mainController = mainController;
        this.transportService = transportService;
    }


    public void handler(Message message) {
        var transportStep = saveUser(message.getChatId());
        var mainStep =mainController.saveUser(message.getChatId());

        if (transportStep.getStep() == null) {
            transportStep.setStep(Step.TRANSPORT);
            System.out.println("123");
            return;
        }

        if (transportStep.getStep().equals(Step.TRANSPORT)) {
            switch (message.getText()) {
                case ButtonName.transportusluga ->{
                    mainService.transportMenu(message);
                    return;
                }
                case ButtonName.priceList -> {
                    transportService.priceData(message);
                }
                case ButtonName.orderCar -> {
                    transportService.orderCar(message);
                }
                case ButtonName.document -> {
                    transportService.documentData(message);
                    transportStep.setStep(Step.DOCUMENT);
                }
                case ButtonName.backMainMenu -> {
                    mainService.mainMenu(message);
                    mainStep.setStep(Step.MAIN);
                    return;
                }
            }
        }
        if (transportStep.getStep().equals(Step.DOCUMENT)) {
            switch (message.getText()) {
                case ButtonName.backTransportMenu -> {
                    mainService.transportMenu(message);
                    transportStep.setStep(Step.TRANSPORT);
                }
                case ButtonName.dataCar -> mainService.dataCar(message);
                case ButtonName.dataVoditel -> mainService.dataVoditel(message);
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
