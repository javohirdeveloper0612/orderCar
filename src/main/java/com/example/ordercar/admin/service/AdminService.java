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
                "*ADMIN MENUGA XUSH KELIBSIZ*",
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
                    "*Active buyurtmalar mavjud emas*"));
            return;
        }

        for (OrderClientEntity entity : list) {
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
                            InlineButton.row(InlineButton.button("zakasni tugatish ✅", "accept_order")),
                            InlineButton.row(InlineButton.button("Mashina chiqadigan manzil \uD83D\uDCCD", "loc1")),
                            InlineButton.row(InlineButton.button("Mashina boradigan manzil \uD83D\uDCCD", "loc2"))))));

        }
    }

    public void notActiveOrder(Message message) {
        var list = orderClientRepository.findAllByStatus(Status.BLOCK);
        if (list.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*Tugallangan buyurtmalar mavjud emas*"));
            return;
        }

        Map<Long, Object[]> patientData = new TreeMap<Long, Object[]>();

        patientData.put(0L, new Object[]{"ID raqami ", " Ism va Familiyasi", "Telefon raqami",
                "Buyurtma sanasi", "Status", "To'lov turi",});

        for (OrderClientEntity entity : list) {

            if (entity != null) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet spreadsheet;
                spreadsheet = workbook.createSheet("Tugallangan zakaslar");

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

                    FileOutputStream out = new FileOutputStream("tugallanga zakaslar ro`yxati.xlsx");
                    workbook.write(out);
                    out.close();


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        try {
            InputStream inputStream = new FileInputStream("tugallanga zakaslar ro`yxati.xlsx");
            InputFile inputFile = new InputFile();
            inputFile.setMedia(inputStream, "tugallanga zakaslar ro`yxati.xlsx");

            myTelegramBot.send(SendMsg.sendDoc(message.getChatId(), inputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void onlineProfit(Message message) {
        var payment = orderClientRepository.findAllByPayment(Payment.PLASTIK);
        if (payment.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*Online kirimlar ro'yxati mavjud emas*"));
            return;
        }

        Map<Long, Object[]> paymentData = new TreeMap<Long, Object[]>();

        paymentData.put(0L, new Object[]{"ID raqami ", " Ism va Familiyasi", "Telefon raqami",
                "Buyurtma sanasi", "Status", "To'lov turi", "Tolov miqdori"});


        for (OrderClientEntity entity : payment) {
            if (entity != null) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet spreadsheet;
                spreadsheet = workbook.createSheet("Online kirimlar royxati");

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

                    FileOutputStream out = new FileOutputStream("online kirimlar ro`yxati.xlsx");
                    workbook.write(out);
                    out.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }
        try {
            InputStream inputStream = new FileInputStream("online kirimlar ro`yxati.xlsx");
            InputFile inputFile = new InputFile();
            inputFile.setMedia(inputStream, "online kirimlar ro`yxati.xlsx");

            myTelegramBot.send(SendMsg.sendDoc(message.getChatId(), inputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteDriver(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Iltimos Haydovchining telefon raqamini kiriting : *" +
                        "\n*Masalan +998991234567 ✅*"));
    }

    public void addDriver(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Iltimos Haydovchining ism va familiyasini kiriting : *" +
                        "\n*Masalan Ismatov Hamdam ✅*"));
    }

    public void getDriverPhone(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Iltimos Haydovchining telefon raqamini quyidagi shakilda jo'nating :*\n" +
                        "*Masalan +998971234567 ✅*"));
    }

    public void claimMessage(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Muvaffaqqiyatli saqlandi ✅*"));

        }

    public boolean checkPhone(Message message) {
        if (!message.getText().startsWith("+998") || message.getText().length() != 13) {
            myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                    "*Iltimos telefon raqamni quyidagi ko'rinishda kiriting !*" +
                            "*\nMasalan : +998901234567  ✅*"));
            return false;
        }

        for (int i = 0; i < message.getText().length(); i++) {
            if (Character.isAlphabetic(message.getText().charAt(i))) {
                myTelegramBot.send(SendMsg.sendMsgParse(message.getChatId(),
                        "*Iltimos telefon raqamni quyidagi ko'rinishda kiriting !*" +
                                "*\nMasalan : +998901234567  ✅*"));
                return false;
            }
        }

        return true;
    }

    public boolean claimDeleting(Message message) {
        var optional = profileRepository.findByPhone(message.getText());
        if (optional.isEmpty()) {
            myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                    "*" + message.getText() + "*" + " *ushbu raqamga ega haydovchi topilmadi*"));
            return false;
        }
        profileRepository.delete(optional.get());
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Muvaffaqqiyatli o'chirildi ✅*"));
        return true;
    }

    public void setting(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*ADMIN VA HAYDOVCHI BO'LIMI*",
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
                    "*Haydovchilar royxati mavjud emas*"));
            return;
        }


        Map<Long, Object[]> paymentData = new TreeMap<Long, Object[]>();

        paymentData.put(0L, new Object[]{"ID raqami ", " Ism va Familiyasi", "Telefon raqami",
                "ROLE"});


        for (ProfileEntity entity : list) {
            if (entity != null) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet spreadsheet;
                spreadsheet = workbook.createSheet("Haydovchilar royxati");

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

                    FileOutputStream out = new FileOutputStream("haydovchilar ro`yxati.xlsx");
                    workbook.write(out);
                    out.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }

        try {
            InputStream inputStream = new FileInputStream("haydovchilar ro`yxati.xlsx");
            InputFile inputFile = new InputFile();
            inputFile.setMedia(inputStream, "haydovchilar ro`yxati.xlsx");

            myTelegramBot.send(SendMsg.sendDoc(message.getChatId(), inputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void onlineOrder(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Iltimos registratsiyadan o'tkazish uchun telefon raqamni quyidagi shakilda kiriting :*" +
                        "\n*Masalan : +998971234567 ✅*"));
    }

    public void getFullNameOffline(Message message) {
        myTelegramBot.send(SendMsg.sendMsg(message.getChatId(),
                "*Iltimos registratsiyadan o'tkazish uchun ism va familiya kiriting : *" +
                        "\n*Masalan : Ismatov Hamdam *"));
    }
}
