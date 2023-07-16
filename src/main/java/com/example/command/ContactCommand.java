package com.example.command;


import com.example.mytelegram.MyTelegramBot;
import com.example.util.Button;
import com.example.util.ButtonName;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class ContactCommand {

    private final MyTelegramBot myTelegramBot;

    public ContactCommand(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void contactCommand(Message message) {

        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*\uD83D\uDCCC Присылайте нам свои вопросы, жалобы, обращения \uD83D\uDCF2\n" +
                        "  Наши специалисты ответят вам в ближайшее время. \uD83D\uDCDD *" +
                        "\n\nКонтакт для Telegram : " +
                        "\n\nhttps://t.me/romanmirzayev" +
                        "\nhttps://t.me/prins332" +


                        "\n\nТелефоны для связи:" +
                        "" +
                        "\n \uD83D\uDCF2 ++998 99 820 70 74" +
                        "\n \uD83D\uDCF2 ++998 90 332 16 11",

                Button.markup(
                        Button.rowList(
                                Button.row(
                                        Button.button(
                                                ButtonName.backMainMenuGlavniy
                                        )
                                )
                        )
                )));

    }
}
