package com.yournovel;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class NovelBot extends TelegramWebhookBot {
    private final InkLikeEngine engine;

    public NovelBot(InkLikeEngine engine) {
        this.engine = engine;
    }

    @Override
    public String getBotUsername() {
        String username = System.getenv("BOT_USERNAME");
        return username != null ? username : "MissingBotUsername";
    }

    @Override
    public String getBotToken() {
        String token = System.getenv("BOT_TOKEN");
        return token != null ? token : "MissingBotToken";
    }

    @Override
    public String getBotPath() {
        return "webhook";
    }

    private ReplyKeyboardMarkup buildChoicesKeyboard(java.util.List<InkLikeEngine.Choice> choices) {
    ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
    java.util.List<KeyboardRow> rows = new java.util.ArrayList<>();
    for (InkLikeEngine.Choice choice : choices) {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(choice.text));
        rows.add(row);
    }
    keyboard.setKeyboard(rows);
    keyboard.setResizeKeyboard(true);
    return keyboard;
}

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            long chatId = msg.getChatId();
            String text = msg.getText();

            InkLikeEngine.OutputBlock output;
            if (text.equalsIgnoreCase("/start")) {
                engine.resetUser(chatId);
                output = engine.getOutput(chatId);
            } else if (text.equalsIgnoreCase("/reset")) {
                engine.resetUser(chatId);
                output = engine.getOutput(chatId);
                output.text = "Прогрес скинуто!\n\n" + output.text;
            } else if (text.equalsIgnoreCase("/help")) {
            return SendMessage.builder()
                .chatId(chatId)
                .text("Доступні команди:\n\n/start - почати розповідь\n/reset - скинути прогрес\n/help - отримати допомогу")
                .build();
            } else {
                output = engine.choose(chatId, text);
            }

            SendMessage.SendMessageBuilder builder = SendMessage.builder()
                .chatId(chatId)
                .text(output.text);

            if (output.choices != null && !output.choices.isEmpty()) {
                builder.replyMarkup(buildChoicesKeyboard(output.choices));
            }

            return builder.build();
        }
        return null;
    }
}