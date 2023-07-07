package com.example.util;

import lombok.Data;

@Data
public class TelegramUsers {

    private Long chatId;

    private Step step =Step.NONE;
}
