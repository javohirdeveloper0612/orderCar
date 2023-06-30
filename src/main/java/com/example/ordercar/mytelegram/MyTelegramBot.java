package com.example.ordercar.mytelegram;

import com.example.ordercar.admin.controller.AdminController;
import com.example.ordercar.config.BotConfig;
import com.example.ordercar.controller.AuthController;
import com.example.ordercar.controller.CallbackController;
import com.example.ordercar.controller.DriverController;
import com.example.ordercar.controller.MainController;
import com.example.ordercar.enums.Status;
import com.example.ordercar.repository.ProfileRepository;
import com.example.ordercar.service.ProfileService;
import com.example.ordercar.util.SendMsg;
import com.example.ordercar.util.Step;
import com.example.ordercar.util.TelegramUsers;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final MainController mainController;
    private final CallbackController callbackController;
    private final AdminController adminController;

    private final ProfileService profileService;

    private final AuthController authController;

    private final DriverController driverController;

    private final ProfileRepository profileRepository;

    private List<TelegramUsers> usersList = new ArrayList<>();

    @Lazy
    public MyTelegramBot(BotConfig botConfig, MainController mainController,
                         CallbackController callbackController, AdminController adminController, ProfileService profileService, AuthController authController, DriverController driverController, ProfileRepository profileRepository) {
        this.botConfig = botConfig;
        this.mainController = mainController;
        this.callbackController = callbackController;
        this.adminController = adminController;
        this.profileService = profileService;
        this.authController = authController;
        this.driverController = driverController;
        this.profileRepository = profileRepository;
    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMyChatMember()) {
            ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
            String status = newChatMember.getStatus();
            Long id = update.getMyChatMember().getFrom().getId();

            if (status.equals("kicked")) {
                profileRepository.changeVisibleByUserid(id, Status.BLOCK);
            } else if (status.equals("member")) {
                profileRepository.changeVisibleByUserid(id, Status.ACTIVE);
                send(SendMsg.sendMsg(id,"Botni qayta ishga tushirganingizdan xursandmiz"));
            }
            return;
        }

        if (update.hasMessage()) {
            Message message = update.getMessage();
            TelegramUsers users = saveUser(message.getChatId());



            if (profileService.isDriver(message.getChatId())){
                driverController.handler(update);
                return;
            }
            if (message.getChatId() == 1030035146L) {
                adminController.handle(update);
            }

            if (message.hasText() && message.getText().equals("%login77#")) {
                authController.handle(message);
                users.setStep(Step.REGISTER);
                return;
            }
            if (users.getStep().equals(Step.REGISTER)) {
                authController.handle(message);
                return;
            }

             if (message.hasContact() && users.getStep().equals(Step.REGISTER)) {
                authController.handle(message);
            }

            mainController.handler(message);

        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            if (message.getChatId() == 1030035146L) {
                adminController.handle(update);
            } else {
                callbackController.handler(update);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(SendDocument message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Message send(SendLocation message) {
        try {
           return execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Message send(EditMessageText editMessageText) {
        try {
           return (Message) execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public TelegramUsers saveUser(Long chatId) {

        TelegramUsers user = usersList.stream().filter(u -> u.getChatId().equals(chatId)).findAny().orElse(null);
        if (user != null) {
            return user;
        }

        TelegramUsers users = new TelegramUsers();
        users.setChatId(chatId);
        usersList.add(users);

        return users;
    }
}
