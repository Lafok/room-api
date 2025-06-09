package com.example.roomapi.service;

import com.example.roomapi.model.*;
import com.example.roomapi.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Component
public class RoomTelegramBot extends TelegramLongPollingBot {

    private final UsersService usersService;
    private final RoomService roomService;

    public RoomTelegramBot(UsersService usersService, RoomService roomService) {
        this.usersService = usersService;
        this.roomService = roomService;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String chatId = update.getMessage().getChatId().toString();
        String[] parts = update.getMessage().getText().trim().split(" ");
        String command = parts[0];

        String response;

        try {
            switch (command) {
                case "/start":
                    response = """
                            👋 halo!
                            commands:
                            /rooms — Room List
                            /join <room> <user> — join room
                            /leave <user> — leave room
                            /status <user> <ACTIVE|INACTIVE> — change status
                            """;
                    break;

                case "/rooms":
                    response = roomService.getAllRooms().stream()
                            .map(Room::getName)
                            .reduce("Room List:\n", (acc, name) -> acc + "- " + name + "\n");
                    break;

                case "/join":
                    if (parts.length < 3) throw new IllegalArgumentException("Invalid input: /join RoomName UserName");
                    String roomName = parts[1];
                    String userName = parts[2];

                    Room room = roomService.getOrCreateRoom(roomName);
                    Users user = new Users(userName, room);
                    usersService.addUser(user);

                    response = userName + " joined to room " + roomName;
                    break;

                case "/leave":
                    if (parts.length < 2) throw new IllegalArgumentException("Invalid input: /leave UserName");
                    String name = parts[1];
                    usersService.removeByName(name);
                    response = name + " left.";
                    break;

                case "/status":
                    if (parts.length < 3) throw new IllegalArgumentException("Invalid input: /status UserName ACTIVE|INACTIVE");
                    String target = parts[1];
                    UserStatus status = UserStatus.valueOf(parts[2].toUpperCase());
                    usersService.setStatusByName(target, status);
                    response = "User Status " + target + " updated to " + status;
                    break;

                default:
                    response = "Invalid input";
            }

        } catch (Exception e) {
            response = "!!! Error: " + e.getMessage();
        }

        sendMessage(chatId, response);
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "roommanager_bot";
    }

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotToken() {
        return botToken;
    }
}

