package com.example.admin.service;
import com.example.enums.Status;
import com.example.mytelegram.MyTelegramBot;
import com.example.repository.OrderClientRepository;
import com.example.repository.ProfileRepository;
import com.example.util.Button;
import com.example.util.CalendarUtil;
import com.example.util.InlineButton;
import com.example.util.SendMsg;
import com.example.admin.util.ButtonNameAdmin;
import com.example.entity.OrderClientEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class MainServiceAdmin {
    private final MyTelegramBot myTelegramBot;
    private final OrderClientRepository orderClientRepository;
    private final ProfileRepository profileRepository;
    private final CalendarUtil calendarUtil;

    public MainServiceAdmin(MyTelegramBot myTelegramBot,
                            OrderClientRepository orderClientRepository,
                            ProfileRepository profileRepository,
                            CalendarUtil calendarUtil) {
        this.myTelegramBot = myTelegramBot;
        this.orderClientRepository = orderClientRepository;
        this.profileRepository = profileRepository;
        this.calendarUtil = calendarUtil;
    }

    public void activeOrder(Message message) {

        var list = orderClientRepository.findAllByStatus(Status.ACTIVE);

        if (list.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*\n" +
                            "\n" +
                            "- Нет активных заказов*"));
            return;
        }

        for (OrderClientEntity entity : list) {

                myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                        "        *>>>>>>>>>>> Заказ <<<<<<<<<<<* \n" +
                                "\n*ID заказа      : * " + entity.getId() + "" +
                                "" +
                                "\n*Имя и фамилия  : * " + entity.getFullName() + "" +
                                "\n*Номер телефона : * " + entity.getPhone() + "" +
                                "\n*Дата заказа    : * " + entity.getOrderDate() + "" +
                                "\n*Статус         :* " + entity.getStatus() + "" +
                                "\n*Тип оплаты     : * " + entity.getPayment(),
                        InlineButton.keyboardMarkup(
                                InlineButton.rowList(
                                        InlineButton.row(
                                                InlineButton.button("Прием заказа ✅", "accept_order#" + entity.getId())),
                                        InlineButton.row(
                                                InlineButton.button("Адрес, откуда уходит машина \uD83D\uDCCD", "loc1#" + entity.getId())),
                                        InlineButton.row(
                                                InlineButton.button("Пункт назначения автомобиля \uD83D\uDCCD", "loc2#" + entity.getId()))))));

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

    public void claimMessage(Message message) {

        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️\n" +
                        "\n" +
                        "- Успешно сохранено ✅*"));

        }

    public boolean checkPhone(Message message) {

        if (!message.getText().startsWith("+998") || message.getText().length() != 13) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*⬇️\n" +
                            "\n" +
                            "- Пожалуйста, введите номер телефона в форму ниже !*" +
                            "*\n : +998901234567  ✅*"));
            return false;
        }

        for (int i = 0; i < message.getText().length(); i++) {
            if (Character.isAlphabetic(message.getText().charAt(i))) {
                myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                        "*⬇️\n" +
                                "\n" +
                                "- Пожалуйста, введите номер телефона в форму ниже !*" +
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
                "*⬇️\n" +
                        "\n" +
                        "- Удалено успешно ✅*"));

        return true;

    }

    public void setting(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*РАЗДЕЛ АДМИНИСТРАТОРА И ВОДИТЕЛЯ*",
                Button.markup(Button.rowList(
                        Button.row(Button.button(ButtonNameAdmin.addDriver)),
                        Button.row(Button.button(ButtonNameAdmin.deleteDriver)),
                        Button.row(Button.button(ButtonNameAdmin.listOfDriver)),
                        Button.row(Button.button(ButtonNameAdmin.back))
                ))));
    }

}
