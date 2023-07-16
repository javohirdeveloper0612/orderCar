package com.example.service;
import com.example.mytelegram.MyTelegramBot;
import com.example.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class BotinstructionService {


    private final MyTelegramBot myTelegramBot;

    public BotinstructionService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void botInstruction(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "Скоро будет запущен раздел руководства по боту...."));

    }
}
