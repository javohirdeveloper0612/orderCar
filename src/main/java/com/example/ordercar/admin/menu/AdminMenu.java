package com.example.ordercar.admin.menu;
import com.example.ordercar.admin.util.AdminButtonName;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.util.Button;
import com.example.ordercar.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class AdminMenu {

    private final MyTelegramBot myTelegramBot;

    public AdminMenu(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void adminMenu(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Здравствуйте ! Выберите необходимое меню ",
                Button.markup(
                        Button.rowList(
                                Button.row(Button.button(AdminButtonName.phoneOrder), Button.button(AdminButtonName.completedOrderList)),
                                Button.row(Button.button(AdminButtonName.moneyincomehistory), Button.button(AdminButtonName.listofactiveorders)),
                                Button.row(Button.button(AdminButtonName.adminAndDriver))
                        ))));
    }
}
