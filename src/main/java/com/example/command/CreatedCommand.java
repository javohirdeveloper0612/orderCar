package com.example.command;

import com.example.mytelegram.MyTelegramBot;
import com.example.util.Button;
import com.example.util.ButtonName;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class CreatedCommand {

    private final MyTelegramBot myTelegramBot;

    public CreatedCommand(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void creativeTeamCommand(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "⬇️\n" +
                        "\n" +
                        "*- Bot yaratuvchilari...\n" +
                        "\n" +
                        "\uD83D\uDD37  UZDP GROUP...\n" +
                        "\n" +
                        "- Takrorlanmas loyihalar biz bilan...\n" +
                        "- Mijozlar bilan ishonchli kelishuv...\n" +
                        "- Doimiy qo'llab quvatlash...\n" +
                        "- Didlaynga amal qilish...\n" +
                        "- Tajribali jamoa...\n" +
                        "\n" +
                        "- Aloqa uchun +998951024055*",

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
