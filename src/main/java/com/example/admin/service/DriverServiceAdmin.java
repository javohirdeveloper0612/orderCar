package com.example.admin.service;
import com.example.entity.ProfileEntity;
import com.example.enums.ProfileRole;
import com.example.mytelegram.MyTelegramBot;
import com.example.repository.ProfileRepository;
import com.example.util.SendMsg;
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
public class DriverServiceAdmin {

    private final MyTelegramBot myTelegramBot;
    private final ProfileRepository profileRepository;

    public DriverServiceAdmin(MyTelegramBot myTelegramBot,
                              ProfileRepository profileRepository) {

        this.myTelegramBot = myTelegramBot;
        this.profileRepository = profileRepository;
    }

    public void deleteDriver(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️\n" +
                        "\n" +
                        "- Пожалуйста, введите номер телефона водителя : *" +
                        "\n*Например +998991234567 ✅*"));
    }

    public void addDriver(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️\n" +
                        "\n" +
                        "- Пожалуйста, введите имя и фамилию водителя : *" +
                        "\n*Например Исматов Хамдам ✅*"));
    }

    public void getDriverPhone(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*⬇️\n" +
                        "\n" +
                        "- Пожалуйста, отправьте номер телефона водителя в форму ниже :*\n" +
                        "*Например +998971234567 ✅*"));
    }

    public void listOfDriver(Message message) {

        var list = profileRepository.findAllByRole(ProfileRole.DRIVER);
        if (list.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*- Нет списка драйверов*"));
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
}
