package com.example.command;
import com.example.mytelegram.MyTelegramBot;
import com.example.util.InlineButton;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class LocationCommand {

    private final MyTelegramBot myTelegramBot;

    public LocationCommand(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }


    public void locationCommand(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Наш адрес: город Салар, Кибрайский район, Ташкентская область ...*",
                InlineButton.keyboardMarkup(
                        InlineButton.rowList(
                                InlineButton.row(
                                        InlineButton.button("посмотреть местоположение", "view_loc_command")
                                )
                        )
                )));

    }

}
