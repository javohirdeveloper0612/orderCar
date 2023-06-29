package com.example.ordercar.service;

import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.enums.ProfileStatus;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.repository.OrderClientRepository;
import com.example.ordercar.util.Button;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.InlineButton;
import com.example.ordercar.util.SendMsg;
import com.google.common.collect.Table;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class DriverService {

    private final MyTelegramBot myTelegramBot;

    private final OrderClientRepository repository;

    @Lazy
    public DriverService(MyTelegramBot myTelegramBot, OrderClientRepository repository) {
        this.myTelegramBot = myTelegramBot;
        this.repository = repository;
    }

    public void menu(Message message) {

        myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                "*Здравствуйте ! Выберите необходимое меню *",
                Button.markup(
                        Button.rowList(
                                Button.row(Button.button(ButtonName.activeOrder),
                                        Button.button(ButtonName.notActiveOrder))
                        ))));
    }

    public void orderList(Message message) {

        boolean check = false;

        Iterable<OrderClientEntity> orderClientList = repository.findAllByStatus(ProfileStatus.BLOCK);

        Map<Long, Object[]> patientData = new TreeMap<Long, Object[]>();

        patientData.put(0L, new Object[]{"ID raqami ", " Ism va Familiyasi", "Telefon raqami",
                "Qavat raqami", "Xona raqami", "Registratsiya Sanasi", "Status"});

        for (OrderClientEntity orderClient : orderClientList) {

            if (orderClient != null) {

                check = true;

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet spreadsheet = workbook.createSheet("Bemorlar royhati");

                XSSFRow row;

                patientData.put(orderClient.getId(), new Object[]{orderClient.getId().toString(), orderClient.getFullName(),
                        orderClient.getPhone(), orderClient.getOrderDate(), orderClient.getCashOrOnline()});
                Set<Long> keyid = patientData.keySet();

                int rowid = 0;
                for (Long key : keyid) {
                    row = spreadsheet.createRow(rowid++);
                    Object[] objectArr = patientData.get(key);
                    int cellid = 0;

                    for (Object obj : objectArr) {
                        XSSFCell cell = row.createCell(cellid++);
                        cell.setCellValue((String) obj);
                    }

                }

                try {

                    FileOutputStream out = new FileOutputStream("Bemorlar ro`yxati.xlsx");
                    workbook.write(out);
                    out.close();


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (!check) {

            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Buyurtmalar ro'yxati mavjud emas*"
            ));
        } else {
            try {
                InputStream inputStream = new FileInputStream("Buyurtmalar ruyhati.xlsx");
                InputFile inputFile = new InputFile();
                inputFile.setMedia(inputStream, "Bemorlar ro`yxati.xlsx");

                myTelegramBot.send(SendMsg.sendDoc(message.getChatId(), inputFile
                ));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendOrder(Message message) {

        var orderClientList = repository.findAllByStatus(ProfileStatus.ACTIVE);

        if (orderClientList.isEmpty()){
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),"*Hozirda Active buyurtmalar mavjud emas*"));
        }

        for (OrderClientEntity entity : orderClientList) {

            myTelegramBot.send(SendMsg.sendMsg(5530157790L,
                    "        *>>>>>>>>>>>Buyurtma<<<<<<<<<<<* \n" +
                            "\n*Buyurtma ID : * " + entity.getId() +
                            "" +
                            "\n*ISM VA FAMILIYA : * " + entity.getFullName() + "" +
                            "\n*TELEFON RAQAM : * " + entity.getPhone() + "" +
                            "\n*Buyurtma sanasi : * " + entity.getOrderDate() + "" +
                            "\n*Status :* " + entity.getStatus() + "" +
                            "\n*To'lov turi : * " + entity.getCashOrOnline(),
                    InlineButton.keyboardMarkup(InlineButton.rowList(
                            InlineButton.row(InlineButton.button("zakasni tugatish ✅", "zakas")),
                            InlineButton.row(InlineButton.button("Mashina chiqadigan manzil \uD83D\uDCCD", "loc1")),
                            InlineButton.row(InlineButton.button("Mashina boradigan manzil \uD83D\uDCCD", "loc2"))))));


        }
    }
}
