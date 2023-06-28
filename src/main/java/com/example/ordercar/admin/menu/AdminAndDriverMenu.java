package com.example.ordercar.admin.menu;
import com.example.ordercar.admin.util.AdminButtonName;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.Button;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class AdminAndDriverMenu {

    private final MyTelegramBot myTelegramBot;

    public AdminAndDriverMenu(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void adminAndDriverMenu(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Выберите необходимое меню  ↘️",
                Button.markup(
                        Button.rowList(
                                Button.row(Button.button(AdminButtonName.adminButton),
                                        Button.button(AdminButtonName.driverButton)),
                                Button.row(Button.button(AdminButtonName.adminbcaktoMenu))

                        ))));
    }

    public void adminAddDeleteList(Message message){

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Выберите нужный вам раздел  ↘️",
                Button.markup(
                        Button.rowList(
                                Button.row(Button.button(AdminButtonName.addAdmin),
                                        Button.button(AdminButtonName.deleteAdmin)),
                                Button.row(Button.button(AdminButtonName.listAdmin)),
                                Button.row(Button.button(AdminButtonName.adminbcaktoMenu))

                        ))));

    }

    public void driverAddDeleteList(Message message){

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Выберите нужный вам раздел  ↘️",
                Button.markup(
                        Button.rowList(
                                Button.row(Button.button(AdminButtonName.addDriver),
                                        Button.button(AdminButtonName.deleteDriver)),
                                Button.row(Button.button(AdminButtonName.listDriver)),
                                Button.row(Button.button(AdminButtonName.adminbcaktoMenu))

                        ))));

    }

    public void adminAddedBackToMenu(Message message){

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Возвращаться", Button.markup(
                        Button.rowList(Button.row(Button.button(AdminButtonName.adminbcaktoMenu))

                        ))));


    }

}

