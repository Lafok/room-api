// src/main/java/com/example/roomapi/service/RoomTelegramBot.java

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class RoomTelegramBot extends TelegramLongPollingBot {

    // BUTTON CONSTANTS
    private static final String ROOMS_BUTTON = "📋 Rooms";
    private static final String USERS_BUTTON = "👥 Users";
    private static final String HELP_BUTTON = "❓ Help";
    private static final String CREATE_ROOM_BUTTON = "➕ Create Room";
    private static final String DELETE_ROOM_BUTTON = "🗑️ Delete Room";
    private static final String CANCEL_BUTTON = "❌ Cancel";

    private static final String GEMINI_CHAT_BUTTON = "🤖 Chat with Gemini";
    private static final String BACK_TO_MENU_BUTTON = "⬅️ Back to Main Menu";


    private enum ConversationState {
        DEFAULT,
        AWAITING_ROOM_NAME_TO_CREATE,
        AWAITING_ROOM_NAME_TO_DELETE,
        CHATTING_WITH_GEMINI
    }

    private final Map<String, ConversationState> userState = new ConcurrentHashMap<>();
    private final UsersService usersService;
    private final RoomService roomService;
    private final GeminiService geminiService; // Inject GeminiService

    @Value("${telegram.bot.username}")
    private String botUsername;
    @Value("${telegram.bot.token}")
    private String botToken;

    public RoomTelegramBot(UsersService usersService, RoomService roomService, GeminiService geminiService) {
        this.usersService = usersService;
        this.roomService = roomService;
        this.geminiService = geminiService;
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

    private void handleTextCommand(Update update) {
        final String chatId = update.getMessage().getChatId().toString();
        final String messageText = update.getMessage().getText().trim();

        ConversationState currentState = userState.getOrDefault(chatId, ConversationState.DEFAULT);

        if (messageText.equals(CANCEL_BUTTON) || messageText.equals(BACK_TO_MENU_BUTTON)) {
            userState.put(chatId, ConversationState.DEFAULT);
            sendMessage(chatId, "✅ Action cancelled. Returning to the main menu.");
            return;
        }

        String response;
        try {
            switch (currentState) {
                case AWAITING_ROOM_NAME_TO_CREATE:
                    response = handleRoomCreation(chatId, messageText);
                    break;
                case AWAITING_ROOM_NAME_TO_DELETE:
                    response = handleRoomDeletion(chatId, messageText);
                    break;
                case CHATTING_WITH_GEMINI:
                    // While in chat mode, all messages go to Gemini
                    response = handleGeminiChat(chatId, messageText);
                    break;
                case DEFAULT:
                default:
                    response = handleDefaultState(chatId, messageText);
                    break;
            }
        } catch (IllegalArgumentException e) {
            response = "⚠️ Invalid command format. " + e.getMessage();
        } catch (Exception e) {
            if (currentState != ConversationState.CHATTING_WITH_GEMINI) {
                response = "🚨 An error occurred: " + e.getMessage();
            } else {
                response = "🚨 Sorry, I had a problem thinking about that. Please try again.";
            }
            e.printStackTrace(); // debug
        }
        sendMessage(chatId, response);
    }

    /**
     * Handles commands when the user is not in a specific conversation flow.
     */
    private String handleDefaultState(String chatId, String messageText) {
        // handle exact matches for buttons and simple commands.
        switch (messageText) {
            case "/start":
                return "👋 *Welcome to RoomAPI!*\n\nUse the menu below to navigate the bot.";
            case ROOMS_BUTTON:
                return listAllRooms();
            case USERS_BUTTON:
                return listAllUsers();
            case HELP_BUTTON:
                return getHelpText();
            case CREATE_ROOM_BUTTON:
                userState.put(chatId, ConversationState.AWAITING_ROOM_NAME_TO_CREATE);
                return "✍️ Please enter the name for the new room.";
            case DELETE_ROOM_BUTTON:
                userState.put(chatId, ConversationState.AWAITING_ROOM_NAME_TO_DELETE);
                return "🗑️ Please enter the name of the room you want to delete.";
            // Handle Gemini Chat button press
            case GEMINI_CHAT_BUTTON:
                userState.put(chatId, ConversationState.CHATTING_WITH_GEMINI);
                return "🤖 You are now chatting with Gemini AI.\n\nAsk me anything! Type your message below or press 'Back to Main Menu' to exit.";
            default:
                // If no exact match, check for commands with arguments.
                final String[] parts = messageText.split(" ");
                final String command = parts[0];
                switch (command) {
                    case "/join":
                        return joinRoom(parts);
                    case "/leave":
                        return leaveRoom(parts);
                    case "/status":
                        return changeUserStatus(parts);
                    default:
                        return "❌ Unknown command. Please use the menu below or type /help.";
                }
        }
    }

    //Method to handle the conversation with Gemini
    private String handleGeminiChat(String chatId, String message) {
        // You could add a "thinking..." message here for long-running queries if needed
        // sendTypingAction(chatId); // Example
        return geminiService.chat(message);
    }

    private String handleRoomCreation(String chatId, String roomName) {
        roomService.createRoom(roomName);
        userState.put(chatId, ConversationState.DEFAULT); // Reset state
        return "✅ Room '" + roomName + "' created successfully!";
    }

    private String handleRoomDeletion(String chatId, String roomName) {
        roomService.deleteRoomByName(roomName);
        userState.put(chatId, ConversationState.DEFAULT); // Reset state
        return "✅ Room '" + roomName + "' has been deleted.";
    }

    //
    private String listAllRooms() {
        return roomService.getAllRooms().isEmpty() ? "There are no available rooms." : roomService.getAllRooms().stream().map(Room::getName).collect(Collectors.joining("\n• ", "Rooms:\n• ", ""));
    }

    private String listAllUsers() {
        return usersService.getAllUsers().isEmpty() ? "There are no users in any room." : usersService.getAllUsers().stream().map(u -> String.format("%s (Room: %s, Status: %s)", u.getName(), u.getRoom().getName(), u.getStatus())).collect(Collectors.joining("\n• ", "Users:\n• ", ""));
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
        return "Help text...";
    }


    // Telegram API Methods

    // This method now dynamically selects the correct keyboard
    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        // Gemini responses can be long and contain markdown that needs to be parsed.
        message.setParseMode("Markdown");

        ConversationState currentState = userState.getOrDefault(chatId, ConversationState.DEFAULT);
        switch (currentState) {
            case DEFAULT:
                message.setReplyMarkup(getMainMenuKeyboard());
                break;
            case CHATTING_WITH_GEMINI:
                message.setReplyMarkup(getGeminiChatKeyboard());
                break;
            case AWAITING_ROOM_NAME_TO_CREATE:
            case AWAITING_ROOM_NAME_TO_DELETE:
                message.setReplyMarkup(getCancelKeyboard());
                break;
        }

        try {
            execute(message);
        } catch (Exception e) {
            // If markdown parsing fails, send as plain text
            try {
                message.setParseMode(null);
                execute(message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Added Gemini Chat button to the main menu
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
        row2.add(new KeyboardButton(CREATE_ROOM_BUTTON));
        row2.add(new KeyboardButton(DELETE_ROOM_BUTTON));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(HELP_BUTTON));

        // A dedicated row for the Gemini chat button
        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton(GEMINI_CHAT_BUTTON));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        markup.setKeyboard(keyboard);
        return markup;
    }

    private ReplyKeyboardMarkup getCancelKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(CANCEL_BUTTON));
        keyboard.add(row);

        markup.setKeyboard(keyboard);
        return markup;
    }

    // A dedicated keyboard for the Gemini chat interface
    private ReplyKeyboardMarkup getGeminiChatKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false); // Keep it open during conversation

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(BACK_TO_MENU_BUTTON));
        keyboard.add(row);

        markup.setKeyboard(keyboard);
        return markup;
    }
}