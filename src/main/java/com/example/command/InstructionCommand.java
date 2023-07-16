package com.example.command;
import com.example.mytelegram.MyTelegramBot;
import com.example.util.Button;
import com.example.util.ButtonName;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class InstructionCommand {

    private final MyTelegramBot myTelegramBot;

    public InstructionCommand(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void instructionCommand(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Руководство по ботам может быть запущено в ближайшее время...",

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
