package com.example.ordercar.driver.service;
import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.enums.Status;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.repository.OrderClientRepository;
import com.example.ordercar.util.Button;
import com.example.ordercar.util.ButtonName;
import com.example.ordercar.util.InlineButton;
import com.example.ordercar.util.SendMsg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.*;
import java.util.*;

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
                "*Salom ! Kerakli menyuni tanlang *",
                Button.markup(
                        Button.rowList(
                                Button.row(Button.button(ButtonName.activeOrder),
                                        Button.button(ButtonName.notActiveOrder)),
                                Button.row(Button.button(ButtonName.acceptOrder))
                        ))));
    }

    public void orderList(Message message) {

        boolean check = false;

        var orderClientList = repository.findAllByStatusAndDriverId(Status.BLOCK,message.getChatId());
        if (orderClientList.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*Tugatilgan buyurtmalar mavjud emas !!!*"));
            return;
        }

        Map<Long, Object[]> patientData = new TreeMap<Long, Object[]>();



        patientData.put(0L, new Object[]{"Номер ID", "Имя и Фамилия", "Номер телефона",
                "Дата заказа", "Тип оплаты", "Статус"});

        for (OrderClientEntity orderClient : orderClientList) {

            if (orderClient != null) {

                check = true;

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet spreadsheet = workbook.createSheet("Список заказов !");

                XSSFRow row;

                patientData.put(orderClient.getId(), new Object[]{orderClient.getId().toString(), orderClient.getFullName(),
                        orderClient.getPhone(), orderClient.getOrderDate().toString(), orderClient.getPayment().toString(), orderClient.getStatus().toString()});
                Set<Long> keyid = patientData.keySet();

                int rowid = 0;
                for (Long key : keyid) {
                    row = spreadsheet.createRow(rowid++);
                    Object[] objectArr = patientData.get(key);
                    int cellid = 0;

                    for (Object obj : objectArr) {
                        Cell cell = row.createCell(cellid++);
                        cell.setCellValue((String) obj);
                    }

                }

                try {

                    FileOutputStream out = new FileOutputStream("Список заказов.xlsx");
                    workbook.write(out);
                    out.close();


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (!check) {

            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Buyurtmalar ro'yxati mavjud emas !!!*"
            ));
        } else {
            try {
                InputStream inputStream = new FileInputStream("Список заказов.xlsx");
                InputFile inputFile = new InputFile();
                inputFile.setMedia(inputStream, "Список заказов.xlsx");

                myTelegramBot.send(SendMsg.sendDoc(message.getChatId(), inputFile
                ));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void activeOrder(Message message) {

        var orderClientList = repository.findActiveOrders(message.getChatId());

        if (orderClientList.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "*Buyurtmangiz hozirda mavjud emas !!!*"));
            return;
        }

        for (OrderClientEntity entity : orderClientList) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "        *>>>>>>>>>>> Buyurtma <<<<<<<<<<<* \n" +
                            "\n*ID заказа : * " + entity.getId() +
                            "" +
                            "\n*Имя и фамилия : * " + entity.getFullName() + "" +
                            "\n*Номер телефона : * " + entity.getPhone() + "" +
                            "\n*Дата заказа : * " + entity.getOrderDate() + "" +
                            "\n*Статус :* " + entity.getStatus() + "" +
                            "\n*Тип оплаты : * " + entity.getPayment(),
                    InlineButton.keyboardMarkup(InlineButton.rowList(
                            InlineButton.row(InlineButton.button("Buyurtmani yakunlash ✅", "finish_order#" + entity.getId())),
                            InlineButton.row(InlineButton.button("Mashina yukni oladigan manzil \uD83D\uDCCD", "loc1#" + entity.getId())),
                            InlineButton.row(InlineButton.button("Mashina yukni olib boradigan manzil \uD83D\uDCCD", "loc2#" + entity.getId()))))));


        }
    }

    public void getLocation(Message message, String[] parts, Integer messageId) {
        Integer locationMessageId = 0;
        Optional<OrderClientEntity> optional = repository.findById(Long.valueOf(parts[1]));

        if (optional.isPresent()) {

            OrderClientEntity entity = optional.get();
            if (parts[0].equals("loc1")) {
                System.out.println("11");
                locationMessageId = myTelegramBot.send(SendMsg.sendLocation(message.getChatId(), entity.getFromWhere(), messageId)).getMessageId();
            } else if (parts[0].equals("loc2")) {
                System.out.println("22");
                locationMessageId = myTelegramBot.send(SendMsg.sendLocation(message.getChatId(), entity.getToWhere(), messageId)).getMessageId();
            }
            System.out.println(Arrays.toString(parts));
            if (parts.length == 3) {
                myTelegramBot.send(SendMsg.deleteMessage(message.getChatId(), Integer.valueOf(parts[2])));
            }

    myTelegramBot.send(SendMsg.editMessage(message.getChatId(),
            "        *>>>>>>>>>>> Buyurtma <<<<<<<<<<<* \n" +
            "\n*ID заказа : * " + entity.getId() +
            "" +
            "\n*Имя и фамилия : * " + entity.getFullName() + "" +
            "\n*Номер телефона : * " + entity.getPhone() + "" +
            "\n*Дата заказа : * " + entity.getOrderDate() + "" +
            "\n*Статус :* " + entity.getStatus() + "" +
            "\n*Тип оплаты : * " + entity.getPayment(),
            InlineButton.keyboardMarkup(InlineButton.rowList(
            InlineButton.row(InlineButton.button("Завершить заказ ✅", "finish_order#" + entity.getId())),
            InlineButton.row(InlineButton.button("Адрес отправления машины \uD83D\uDCCD", "loc1#" + entity.getId() + "#" + locationMessageId)),
            InlineButton.row(InlineButton.button("Пункт назначения автомобиля \uD83D\uDCCD", "loc2#" + entity.getId() + "#" + locationMessageId)))), messageId));
}
    }


    public void acceptOrderList(Message message) {

        var orderClientList = repository.findOrderByStatus();

        if (orderClientList.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "*Hozirda faol buyurtmalar mavjud emas !!!*"));
            return;
        }

        for (OrderClientEntity entity : orderClientList) {


            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "        *>>>>>>>>>>>Buyurtma<<<<<<<<<<<* \n" +
                            "\n*Buyurtma ID : * " + entity.getId() +
                            "" +
                            "\n*ISM VA FAMILIYA : * " + entity.getFullName() + "" +
                            "\n*TELEFON RAQAM : * " + entity.getPhone() + "" +
                            "\n*Buyurtma sanasi : * " + entity.getOrderDate() + "" +
                            "\n*Status :* " + entity.getStatus() + "" +
                            "\n*To'lov turi : * " + entity.getPayment(),
                    InlineButton.keyboardMarkup(InlineButton.rowList(
                            InlineButton.row(InlineButton.button("Buyurtmani qabul qilish ✅", "accept_order#" + entity.getId()))))));
        }
    }


    public void finishOrder(Message message, long id, Integer messageId) {
        Optional<OrderClientEntity> optional = repository.findById(id);
        if (optional.isPresent()) {
            OrderClientEntity entity = optional.get();
            entity.setStatus(Status.BLOCK);
            repository.save(entity);
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Buyurtma muvaffaqiyatli yakunlandi ✅", messageId));
        }
    }

    public void acceptOrder(Message message, long locationId) {
        Optional<OrderClientEntity> optional = repository.findById(locationId);
        if (optional.isPresent()) {
            OrderClientEntity orderClient = optional.get();

            if (orderClient.getDriverId() != null) {
                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Bu buyurtma boshqa haydovchi tomonidan qabul qilindi !!!"));
                return;
            }

            orderClient.setStatus(Status.ACTIVE);
            orderClient.setDriverId(message.getChatId());
            repository.save(orderClient);
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(), "Buyurtma muvaffaqiyatli qabul qilindi  ✅"));
            activeOrder(message);

        }
    }


}



