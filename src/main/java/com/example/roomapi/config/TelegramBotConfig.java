package com.example.roomapi.config;

import com.example.roomapi.service.RoomTelegramBot;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private final RoomTelegramBot roomTelegramBot;

    public TelegramBotConfig(RoomTelegramBot roomTelegramBot) {
        this.roomTelegramBot = roomTelegramBot;
    }

    @PostConstruct
    public void registerBot() {
        try {
            System.out.println(">>> Registering Telegram bot...");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(roomTelegramBot);
            System.out.println(">>> Bot registered successfully!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
