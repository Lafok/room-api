package com.example.roomapi.service;

import com.example.roomapi.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomTelegramBot extends TelegramLongPollingBot {

    // --- BUTTON CONSTANTS ---
    private static final String ROOMS_BUTTON = "📋 Rooms";
    private static final String USERS_BUTTON = "👥 Users";
    private static final String HELP_BUTTON = "❓ Help";

    private final UsersService usersService;
    private final RoomService roomService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    public RoomTelegramBot(UsersService usersService, RoomService roomService) {
        this.usersService = usersService;
        this.roomService = roomService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextCommand(update);
        }
    }

    /**
     * Handles all incoming text messages, routing them to the correct logic.
     * This is the main router for the bot's functionality.
     */
    private void handleTextCommand(Update update) {
        final String chatId = update.getMessage().getChatId().toString();
        final String messageText = update.getMessage().getText().trim();
        final String[] parts = messageText.split(" ");
        final String command = parts[0];

        String response;
        try {
            // First, handle exact matches for buttons and simple commands
            switch (messageText) {
                case "/start":
                    response = "👋 *Welcome to RoomAPI!*\n\nUse the menu below to navigate the bot.";
                    break;
                case ROOMS_BUTTON:
                    response = listAllRooms();
                    break;
                case USERS_BUTTON:
                    response = listAllUsers();
                    break;
                case HELP_BUTTON:
                    response = getHelpText();
                    break;
                default:
                    // If no exact match, check for commands with arguments
                    switch (command) {
                        case "/join":
                            response = joinRoom(parts);
                            break;
                        case "/leave":
                            response = leaveRoom(parts);
                            break;
                        case "/status":
                            response = changeUserStatus(parts);
                            break;
                        default:
                            response = "❌ Unknown command. Please use the menu below or type /help.";
                    }
            }
        } catch (IllegalArgumentException e) {
            response = "⚠️ Invalid command format. " + e.getMessage();
        } catch (Exception e) {
            response = "🚨 An error occurred: " + e.getMessage();
        }

        sendMessage(chatId, response);
    }

    // --- Command Logic Methods (for better organization) ---

    private String listAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) {
            return "There are no available rooms.";
        }
        return rooms.stream()
                .map(Room::getName)
                .collect(Collectors.joining("\n• ", "Rooms:\n• ", ""));
    }

    private String listAllUsers() {
        List<Users> users = usersService.getAllUsers();
        if (users.isEmpty()) {
            return "There are no users in any room.";
        }
        return users.stream()
                .map(u -> String.format("%s (Room: %s, Status: %s)", u.getName(), u.getRoom().getName(), u.getStatus()))
                .collect(Collectors.joining("\n• ", "Users:\n• ", ""));
    }

    private String joinRoom(String[] parts) {
        if (parts.length < 3) throw new IllegalArgumentException("Format: /join <RoomName> <UserName>");
        String roomName = parts[1];
        String userName = parts[2];

        if (usersService.existsByName(userName)) {
            return "The name '" + userName + "' is already taken.";
        }
        Room room = roomService.getOrCreateRoom(roomName);
        Users user = new Users(userName, room);
        usersService.addUser(user);
        return "✅ " + userName + " has joined the room '" + roomName + "'.";
    }

    private String leaveRoom(String[] parts) {
        if (parts.length < 2) throw new IllegalArgumentException("Format: /leave <UserName>");
        String userName = parts[1];
        usersService.removeByName(userName);
        return "✅ " + userName + " has left the room.";
    }

    private String changeUserStatus(String[] parts) {
        if (parts.length < 3) throw new IllegalArgumentException("Format: /status <UserName> <ACTIVE|INACTIVE>");
        String targetUser = parts[1];
        UserStatus status;
        try {
            status = UserStatus.valueOf(parts[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            return "❌ Invalid status. Please use ACTIVE or INACTIVE.";
        }
        usersService.setStatusByName(targetUser, status);
        return "✅ User '" + targetUser + "' status has been set to " + status + ".";
    }

    private String getHelpText() {
        return """
                *⚙️ Available Commands*
                
                *Buttons:*
                `📋 Rooms` - List all available rooms.
                `👥 Users` - List all users and their status.
                
                *Typed Commands:*
                `/join <room> <name>` - Join a room.
                `/leave <name>` - Leave your room.
                `/status <name> <ACTIVE|INACTIVE>` - Change a user's status.
                """;
    }

    // --- Telegram API Methods ---

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        message.setParseMode("Markdown");
        message.setReplyMarkup(getMainMenuKeyboard());
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(ROOMS_BUTTON));
        row1.add(new KeyboardButton(USERS_BUTTON));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(HELP_BUTTON));

        keyboard.add(row1);
        keyboard.add(row2);

        markup.setKeyboard(keyboard);
        return markup;
    }
}