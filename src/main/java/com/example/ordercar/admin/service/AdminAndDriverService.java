package com.example.ordercar.admin.service;
import com.example.ordercar.admin.entity.ProfileEntity;
import com.example.ordercar.admin.enums.ProfileRole;
import com.example.ordercar.admin.repostoriy.ProfileRepostoriy;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

@Service
public class AdminAndDriverService {

    private final MyTelegramBot myTelegramBot;

    private final ProfileRepostoriy profileRepostoriy;

    public AdminAndDriverService(MyTelegramBot myTelegramBot, ProfileRepostoriy profileRepostoriy) {
        this.myTelegramBot = myTelegramBot;
        this.profileRepostoriy = profileRepostoriy;
    }

    public void adminFullName(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Введите имя и фамилию администратора, которого вы добавляете  ⬇️"));
    }

    public void adminPhoneNumber(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Введите номер телефона администратора, которого вы добавляете  ⬇️"));
    }

    public void addedAdmin(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Админ добавлен, теперь ваш добавленный админ может войти в кабинет администратора по номеру телефона  ✅"));
    }

    public boolean checkPhone(Message message) {
        if (!message.getText().startsWith("+998") || message.getText().length() != 13) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "Пожалуйста, введите номер телефона в форму ниже !!!" +
                            "\nНапример : +998951024055  ✅"));
            return false;
        }
        return true;
    }

    public boolean adminList(Message message) {

        List<ProfileEntity> profileEntityList = (List<ProfileEntity>) profileRepostoriy.findByProfileRole(ProfileRole.ADMIN);

        if(profileEntityList.isEmpty()){
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"Извините, списка администраторов пока нет  ❌"));
            return false;
        }

        for (ProfileEntity profileEntity : profileEntityList) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "➡️  ИД Номер   :   "+profileEntity.getId()+"\n" +
                            "\uD83D\uDCCC  Имя              :   "+profileEntity.getFull_name()+"\n" +
                            "\uD83D\uDCDE  Телефон      :   "+profileEntity.getPhone()));

        }

        return true;

    }

    public void adminIdNumber(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"Введите идентификационный номер администратора, которого вы хотите удалить  ⬇️"));
    }

    public boolean deleteAdmin(Message message) {

        var text = message.getText();

        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i)) || Character.isLetter(text.charAt(i))) {
                myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                        "ID не может содержать буквы или символы, введите число  ❌"));
                return false;
            }
        }

        Long id = Long.valueOf(message.getText());
        Optional<ProfileEntity> optional = profileRepostoriy.findByIdAndProfileRole(id,ProfileRole.ADMIN);

        if(optional.isEmpty()){
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(), "Извините, такого идентификатора не существует  ❌" +
                    "введите другой   ⬇️"));
            return false;
        }

        ProfileEntity profileEntity = optional.get();
        profileRepostoriy.deleteById(profileEntity.getId());
        return true;

    }

    public void deleteAdminOk(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"Администратор отключен  \uD83D\uDDD1"));
    }

    public void driverFullName(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Введите имя и фамилию Водитель, которого вы добавляете  ⬇️"));
    }

    public void driverPhoneNumber(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Введите номер телефона Водитель, которого вы добавляете  ⬇️"));
    }

    public void addedDriver(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Водитель добавлен, теперь ваш добавленный админ может войти в кабинет администратора по номеру телефона  ✅"));
    }

    public void driverIdNumber(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"Введите идентификационный номер Водитель, которого вы хотите удалить  ⬇️"));

    }

    public boolean driverList(Message message) {


        List<ProfileEntity> profileEntityList = profileRepostoriy.findByProfileRole(ProfileRole.DRIVER);

        if(profileEntityList.isEmpty()){
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"Извините, списка администраторов пока нет  ❌"));
            return false;
        }

        for (ProfileEntity profileEntity : profileEntityList) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "➡️  ИД Номер   :   "+profileEntity.getId()+"\n" +
                            "\uD83D\uDCCC  Имя              :   "+profileEntity.getFull_name()+"\n" +
                            "\uD83D\uDCDE  Телефон      :   "+profileEntity.getPhone()));

        }

        return true;
    }

    public boolean deleteDriver(Message message) {

        var text = message.getText();

        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i)) || Character.isLetter(text.charAt(i))) {
                myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                        "ID не может содержать буквы или символы, введите число  ❌"));
                return false;
            }
        }

        Long id = Long.valueOf(message.getText());
        Optional<ProfileEntity> optional = profileRepostoriy.findByIdAndProfileRole(id,ProfileRole.DRIVER);

        if(optional.isEmpty()){
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(), "Извините, такого идентификатора не существует  ❌" +
                    "введите другой   ⬇️"));
            return false;
        }

        ProfileEntity profileEntity = optional.get();
        profileRepostoriy.deleteById(profileEntity.getId());
        return true;

    }

    public void deleteDriverOk(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"Водитель отключен  \uD83D\uDDD1"));

    }
}
