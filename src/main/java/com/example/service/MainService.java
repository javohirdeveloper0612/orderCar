package com.example.service;

import com.example.mytelegram.MyTelegramBot;
import com.example.util.Button;
import com.example.util.ButtonName;
import com.example.util.InlineButton;
import com.example.util.SendMsg;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class MainService {

    private final MyTelegramBot myTelegramBot;

    @Lazy
    public MainService(MyTelegramBot myTelegramBot) {
        this.myTelegramBot = myTelegramBot;
    }

    public void mainMenu(Long chatId) {
        myTelegramBot.send(SendMsg.sendMsgParse(chatId,
                "*Здравствуйте ! Выберите необходимое меню *",
                Button.markup(
                        Button.rowList(
                                Button.row(Button.button(ButtonName.transportusluga)),
                                Button.row(Button.button(ButtonName.metallBuyum),
                                        Button.button(ButtonName.metallprokat)),
                                Button.row(Button.button(ButtonName.location),
                                        Button.button(ButtonName.contact)),
                                Button.row(Button.button(ButtonName.setting),
                                        Button.button(ButtonName.botinstruction))
                        ))));
    }

    public void transportMenu(Message message) {
        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Транспорт услуга меню*"
                , Button.markup(Button.rowList(
                        Button.row(Button.button(ButtonName.orderCar)),
                        Button.row(Button.button(ButtonName.priceList)),
                        Button.row(Button.button(ButtonName.document)),
                        Button.row(Button.button(ButtonName.backMainMenu))
                ))));
    }

    public void metallBuyumMenu(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Эта функция скоро появится ! *",

                Button.markup(
                        Button.rowList(
                                Button.row(
                                        Button.button(
                                                ButtonName.backMainMenu
                                        )
                                )
                        )
                )));
    }

    public void metalProkatMenu(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Эта функция будет доступна в ближайшее время *",

                Button.markup(
                        Button.rowList(
                                Button.row(
                                        Button.button(
                                                ButtonName.backMainMenu
                                        )
                                )
                        )
                )));
    }


    public void dataCar(Message message) {
        myTelegramBot.send(
                SendMsg.sendDocument(message.getChatId(), "Информация о транспорте",
                        "BQACAgIAAxkBAAIHnmSZPWXcd7EXVviPMH2QW9EVoKjvAAJzLAACTL3RSPBUeV_sr_P8LwQ")
        );
    }

    public void dataVoditel(Message message) {
        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "*Эта функция скоро появится*")
        );
    }

    public void contact(Message message) {

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
                                                ButtonName.backMainMenu
                                        )
                                )
                        )
                )));
    }

    public void location(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Наш адрес: город Салар, Кибрайский район, Ташкентская область ...*",
                InlineButton.keyboardMarkup(
                        InlineButton.rowList(
                                InlineButton.row(
                                        InlineButton.button("посмотреть местоположение", "view_loc")
                                )
                        )
                )));
    }

    public void setting(Message message) {

        myTelegramBot.send(
                SendMsg.sendMsg(message.getChatId(), "*Эта функция скоро появится*",

                        Button.markup(
                                Button.rowList(
                                        Button.row(
                                                Button.button(
                                                        ButtonName.backMainMenu
                                                )
                                        )
                                )
                        ))
        );
    }
}

