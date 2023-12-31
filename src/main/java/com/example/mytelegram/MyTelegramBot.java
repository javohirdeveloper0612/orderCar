package com.example.mytelegram;
import com.example.driver.controller.DriverController;
import com.example.repository.ProfileRepository;
import com.example.admin.controller.AdminController;
import com.example.config.BotConfig;
import com.example.controller.AuthController;
import com.example.controller.CallbackController;
import com.example.controller.MainController;
import com.example.enums.Status;
import com.example.service.ProfileService;
import com.example.util.SendMsg;
import com.example.util.Step;
import com.example.util.TelegramUsers;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
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
                         CallbackController callbackController, AdminController adminController,
                         ProfileService profileService, AuthController authController,
                         DriverController driverController, ProfileRepository profileRepository) {

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
                send(SendMsg.sendMsg(id, "Рад, что бот снова заработал !"));

            }
            return;
        }

        if (update.hasMessage()) {

            Message message = update.getMessage();
            TelegramUsers users = saveUser(message.getChatId());


            if (profileService.isDriver(message.getChatId())) {
                driverController.handler(update);
                return;
            }

            if (message.getChatId() == 60425361L) {
                adminController.handle(update);
                return;
            }

            if (message.hasText() && message.getText().equals("*7777#")) {
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

            if (message.getChatId() == 5530157798L) {

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
    public Message send(SendPhoto message) {
        try {
         return  execute(message);
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

    public Boolean send(DeleteMessage deleteMessage) {
        try {
         return  execute(deleteMessage);
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
