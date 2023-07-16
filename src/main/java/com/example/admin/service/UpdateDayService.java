package com.example.admin.service;
import com.example.mytelegram.MyTelegramBot;
import com.example.repository.OrderClientRepository;
import com.example.util.CalendarUtil;
import org.springframework.stereotype.Service;



@Service
public class UpdateDayService {

    private final MyTelegramBot myTelegramBot;
    private final CalendarUtil calendarUtil;
    private final OrderClientRepository orderClientRepository;


    public UpdateDayService(MyTelegramBot myTelegramBot,
                            CalendarUtil calendarUtil,
                            OrderClientRepository orderClientRepository) {

        this.myTelegramBot = myTelegramBot;
        this.calendarUtil = calendarUtil;
        this.orderClientRepository = orderClientRepository;
    }

}
