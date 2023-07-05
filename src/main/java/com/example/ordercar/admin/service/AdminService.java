package com.example.ordercar.admin.service;
import com.example.ordercar.admin.button.ButtonName;
import com.example.ordercar.entity.OrderClientEntity;
import com.example.ordercar.entity.ProfileEntity;
import com.example.ordercar.enums.Payment;
import com.example.ordercar.enums.ProfileRole;
import com.example.ordercar.enums.Status;
import com.example.ordercar.mytelegram.MyTelegramBot;
import com.example.ordercar.repository.OrderClientRepository;
import com.example.ordercar.repository.ProfileRepository;
import com.example.ordercar.util.Button;
import com.example.ordercar.util.InlineButton;
import com.example.ordercar.util.SendMsg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class AdminService {
    private final MyTelegramBot myTelegramBot;
    private final OrderClientRepository orderClientRepository;
    private final ProfileRepository profileRepository;

    public AdminService(MyTelegramBot myTelegramBot,
                        OrderClientRepository orderClientRepository,
                        ProfileRepository profileRepository) {
        this.myTelegramBot = myTelegramBot;
        this.orderClientRepository = orderClientRepository;
        this.profileRepository = profileRepository;
    }

    public void mainMenu(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*ДОБРО ПОЖАЛОВАТЬ В МЕНЮ АДМИНИСТРАТОРА*",
                Button.markup(Button.rowList(

                        Button.row(Button.button(ButtonName.onlineOrder)),
                        Button.row(
                                Button.button(ButtonName.activeOrder),
                                Button.button(ButtonName.notactiveOrder)
                        ),

                        Button.row(Button.button(ButtonName.onlineProfit),
                                Button.button(ButtonName.setting))
                ))));

    }

    public void activeOrder(Message message) {
        var list = orderClientRepository.findAllByStatus(Status.ACTIVE);
        if (list.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*Нет активных заказов*"));
            return;
        }

        for (OrderClientEntity entity : list) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "        *>>>>>>>>>>>Заказ<<<<<<<<<<<* \n" +
                            "\n*ID заказа : * " + entity.getId() +
                            "" +
                            "\n*Имя и фамилия : * " + entity.getFullName() + "" +
                            "\n*Номер телефона : * " + entity.getPhone() + "" +
                            "\n*Дата заказа : * " + entity.getOrderDate() + "" +
                            "\n*Статус :* " + entity.getStatus() + "" +
                            "\n*Тип оплаты : * " + entity.getPayment(),
                    InlineButton.keyboardMarkup(InlineButton.rowList(
                            InlineButton.row(InlineButton.button("Завершить заказ ✅", "accept_order")),
                            InlineButton.row(InlineButton.button("Адрес, откуда уходит машина \uD83D\uDCCD", "loc1")),
                            InlineButton.row(InlineButton.button("Пункт назначения автомобиля \uD83D\uDCCD", "loc2"))))));

        }
    }

    public void notActiveOrder(Message message) {
        var list = orderClientRepository.findAllByStatus(Status.BLOCK);
        if (list.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*Выполненные заказы недоступны*"));
            return;
        }

        Map<Long, Object[]> patientData = new TreeMap<Long, Object[]>();


        patientData.put(0L, new Object[]{"Номер ID", "Имя и Фамилия", "Номер телефона",
                "Дата заказа","Статус" , "Тип оплаты" , });

        for (OrderClientEntity entity : list) {

            if (entity != null) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet spreadsheet;
                spreadsheet = workbook.createSheet("Список заказов");

                XSSFRow row;

                patientData.put(entity.getId(), new Object[]{entity.getId().toString(), entity.getFullName(),
                        entity.getPhone(), entity.getOrderDate().toString(), entity.getStatus().toString(),
                        entity.getPayment().toString()});

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

        try {
            InputStream inputStream = new FileInputStream("Список заказов.xlsx");
            InputFile inputFile = new InputFile();
            inputFile.setMedia(inputStream, "Список заказов.xlsx");

            myTelegramBot.send(SendMsg.sendDoc(message.getChatId(), inputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void onlineProfit(Message message) {
        var payment = orderClientRepository.findAllByPayment(Payment.PLASTIK);
        if (payment.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*Нет онлайн-списка ввода*"));
            return;
        }

        Map<Long, Object[]> paymentData = new TreeMap<Long, Object[]>();

        paymentData.put(0L, new Object[]{"Номер ID", "Имя и Фамилия", "Номер телефона",
                "Дата заказа","Статус" , "Тип оплаты" , "К оплате"});


        for (OrderClientEntity entity : payment) {
            if (entity != null) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet spreadsheet;
                spreadsheet = workbook.createSheet("список онлайн-заявок");

                XSSFRow row;

                paymentData.put(entity.getId(), new Object[]{entity.getId().toString(), entity.getFullName(),
                        entity.getPhone(), entity.getOrderDate(), entity.getStatus(),
                        entity.getPayment().toString(), entity.getOnlineMoney()});

                Set<Long> keyid = paymentData.keySet();
                int rowid = 0;
                for (Long key : keyid) {
                    row = spreadsheet.createRow(rowid++);
                    Object[] objectArr = paymentData.get(key);
                    int cellid = 0;

                    for (Object obj : objectArr) {
                        Cell cell = row.createCell(cellid++);
                        cell.setCellValue((String) obj);
                    }
                }

                try {

                    FileOutputStream out = new FileOutputStream("список онлайн-заявок.xlsx");
                    workbook.write(out);
                    out.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }
        try {
            InputStream inputStream = new FileInputStream("список онлайн-заявок.xlsx");
            InputFile inputFile = new InputFile();
            inputFile.setMedia(inputStream, "список онлайн-заявок.xlsx");

            myTelegramBot.send(SendMsg.sendDoc(message.getChatId(), inputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteDriver(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Пожалуйста, введите номер телефона водителя : *" +
                        "\n*Например +998991234567 ✅*"));
    }

    public void addDriver(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Пожалуйста, введите имя и фамилию водителя : *" +
                        "\n*Например Исматов Хамдам ✅*"));
    }

    public void getDriverPhone(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Пожалуйста, отправьте номер телефона водителя в форму ниже :*\n" +
                        "*Например +998971234567 ✅*"));
    }

    public void claimMessage(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Успешно сохранено ✅*"));

        }

    public boolean checkPhone(Message message) {
        if (!message.getText().startsWith("+998") || message.getText().length() != 13) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Пожалуйста, введите номер телефона в форму ниже !*" +
                            "*\n : +998901234567  ✅*"));
            return false;
        }

        for (int i = 0; i < message.getText().length(); i++) {
            if (Character.isAlphabetic(message.getText().charAt(i))) {
                myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                        "*Пожалуйста, введите номер телефона в форму ниже !*" +
                                "*\nНапример : +998901234567  ✅*"));
                return false;
            }
        }

        return true;
    }

    public boolean claimDeleting(Message message) {
        var optional = profileRepository.findByPhone(message.getText());
        if (optional.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*" + message.getText() + "*" + " *Водитель с таким номером не найден*"));
            return false;
        }
        profileRepository.delete(optional.get());
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Удалено успешно ✅*"));
        return true;
    }

    public void setting(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*РАЗДЕЛ АДМИНИСТРАТОРА И ВОДИТЕЛЯ*",
                Button.markup(Button.rowList(
                        Button.row(Button.button(ButtonName.addDriver)),
                        Button.row(Button.button(ButtonName.deleteDriver)),
                        Button.row(Button.button(ButtonName.listOfDriver)),
                        Button.row(Button.button(ButtonName.back))
                ))));
    }

    public void listOfDriver(Message message) {

        var list = profileRepository.findAllByRole(ProfileRole.DRIVER);
        if (list.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*Нет списка драйверов*"));
            return;
        }


        Map<Long, Object[]> paymentData = new TreeMap<Long, Object[]>();

        paymentData.put(0L, new Object[]{"Номер ID", "Имя и Фамилия", "Номер телефона", "РОЛЬ"});


        for (ProfileEntity entity : list) {
            if (entity != null) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet spreadsheet;
                spreadsheet = workbook.createSheet("Список водителей");

                XSSFRow row;

                paymentData.put(entity.getId(), new Object[]{entity.getId().toString(), entity.getFullName(),
                        entity.getPhone(), entity.getRole().toString()});

                Set<Long> keyid = paymentData.keySet();
                int rowid = 0;
                for (Long key : keyid) {
                    row = spreadsheet.createRow(rowid++);
                    Object[] objectArr = paymentData.get(key);
                    int cellid = 0;

                    for (Object obj : objectArr) {
                        Cell cell = row.createCell(cellid++);
                        cell.setCellValue((String) obj);
                    }
                }

                try {

                    FileOutputStream out = new FileOutputStream("Список водителей.xlsx");
                    workbook.write(out);
                    out.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }

        try {
            InputStream inputStream = new FileInputStream("Список водителей.xlsx");
            InputFile inputFile = new InputFile();
            inputFile.setMedia(inputStream, "Список водителей.xlsx");

            myTelegramBot.send(SendMsg.sendDoc(message.getChatId(), inputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void onlineOrder(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Пожалуйста, введите свой номер телефона в форму ниже, чтобы зарегистрироваться :*" +
                        "\n*Например : +998971234567 ✅*"));
    }

    public void getFullNameOffline(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Пожалуйста, введите ваше имя и фамилию для регистрации : *" +
                        "\n* Например: Ismatov Hamdam *"));
    }
}
