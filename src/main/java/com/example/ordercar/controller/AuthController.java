package com.example.ordercar.controller;

import com.example.ordercar.entity.ProfileEntity;
import com.example.ordercar.enums.Status;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.service.AuthService;
import com.example.ordercar.util.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
@Controller
public class AuthController {


    private final MyTelegramBot myTelegramBot;

    private final AuthService authService;


    private List<TelegramUsers> usersList = new ArrayList<>();
    ProfileEntity profileEntity = new ProfileEntity();

    @Lazy
    public AuthController(MyTelegramBot myTelegramBot, AuthService authService) {
        this.myTelegramBot = myTelegramBot;
        this.authService = authService;
    }


    public void handle(Message message) {


        TelegramUsers users = saveUser(message.getChatId());

        TelegramUsers stepMain = myTelegramBot.saveUser(message.getChatId());

        if (message.hasText()) {


            if (users.getStep().equals(Step.NONE)) {
                profileEntity.setFullName(message.getText());

                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                        "Iltimos raqamingizni yuboring",
                        Button.markup(Button.rowList(Button.row(
                                Button.button()
                        )))));
                users.setStep(Step.PHONE);
                return;
            }

            if (users.getStep().equals(Step.PHONE)) {

                if (!checkPhone(message.getText())) {
                    myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                            "Iltimos telefon raqamni to'g'ri kiriting ! "));
                    return;
                }

                if (!checkPhoneExists(message.getText())) {
                    myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                            "Bu raqam ro'yxatdan o'tmagan \n" +
                                    "Iltimos qaytadan kiriting "));
                    return;
                }

                String smsCode  = RandomUtil.getRandomNumber();
                profileEntity.setSmsCode(MD5.md5(smsCode));
                profileEntity.setStatus(Status.NOTACTIVE);
                profileEntity.setPhone(message.getText());

                authService.createProfile(profileEntity);
                SmsServiceUtil.sendSmsCode(SmsServiceUtil.removePlusSign(message.getText()),smsCode);

                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"Tasdiqlash xabar yuborildi kodni kiriting !"));

                users.setStep(Step.PASSWORD);
                return;
            }

            if (users.getStep().equals(Step.PASSWORD)) {


                if (!profileEntity.getSmsCode().equals(MD5.md5(message.getText()))) {
                    myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                            "Parolni xato kiritdingiz iltimos qaytadan urinib koring ! "));
                    return;
                }
                System.out.println(profileEntity.getPhone());
                authService.saveUserId(profileEntity.getPhone(),message.getChatId());

                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                        "Muvaffaqiyatli ruyhatdan o'tdingiz",
                        Button.markup(Button.rowList(
                                Button.row(Button.button("Asosiy Menyu !"))
                        ))));
                users.setStep(Step.NONE);
                stepMain.setStep(Step.NONE);
                profileEntity = new ProfileEntity();
            }

            return;
        }

        if (message.hasContact()) {

            if (users.getStep().equals(Step.PHONE)) {

                if (!checkPhoneExists(message.getContact().getPhoneNumber())) {
                    myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                            "Bu raqam ro'yxatdan o'tmagan \n" +
                                    "Iltimos qaytadan kiriting "));
                    return;
                }

                String smsCode = RandomUtil.getRandomNumber();
                profileEntity.setSmsCode(MD5.md5(smsCode));
                profileEntity.setStatus(Status.NOTACTIVE);
                profileEntity.setPhone(message.getContact().getPhoneNumber());

                authService.createProfile(profileEntity);
                SmsServiceUtil.sendSmsOrder(SmsServiceUtil.removePlusSign(message.getContact().getPhoneNumber()),smsCode);

                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"Tasdiqlash xabar yuborildi kodni kiriting !"));

                users.setStep(Step.PASSWORD);

            }
        }


    }


    public boolean isExists(Message message) {
        return authService.isExists(message.getChatId());
    }

    public boolean checkPhone(String text) {
        if (text.startsWith("+998") && text.length() == 13 || text.startsWith("998") && text.length() == 12) {

            return true;
        }
        return false;
    }

    public boolean checkPhoneExists(String text) {
        return authService.isExists(text);
    }

    public TelegramUsers saveUser(Long chatId) {

        for (TelegramUsers users : usersList) {
            if (users.getChatId().equals(chatId)) {
                return users;
            }
        }


        TelegramUsers users = new TelegramUsers();
        users.setChatId(chatId);
        usersList.add(users);

        return users;
    }
}
