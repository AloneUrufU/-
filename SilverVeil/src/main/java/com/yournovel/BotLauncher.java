package com.yournovel;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotLauncher {
    public static void main(String[] args) throws Exception {
        InkLikeEngine engine = new InkLikeEngine();
        NovelBot bot = new NovelBot(engine);

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot, SetWebhook.builder()
                .url(System.getenv("WEBHOOK_URL")) // наприклад, https://your-app.fly.dev/webhook
                .build());
    }
}