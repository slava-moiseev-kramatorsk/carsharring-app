package project.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import project.telegram.TelegramBot;

@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final TelegramBot telegramBot;

    @PostConstruct
    public void start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register telegram bot", e);
        }
    }
}

