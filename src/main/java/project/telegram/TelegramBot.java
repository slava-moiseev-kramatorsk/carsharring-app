package project.telegram;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.model.User;
import project.repository.user.UserRepository;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static final String TELEGRAM_API_KEY = Dotenv.load().get("TELEGRAM_API_KEY");
    private static final String BOT_USER_NAME = Dotenv.load().get("BOT_USER_NAME");
    private static final String ERROR_MESSAGE = "Error sending notification ";
    private static final String START_MESSAGE = "/start";
    private static final String WELCOME_MESSAGE = "Welcome to our chat. "
            + "If you want to receive notifications, enter your email.";
    private static final String INFORM_MESSAGE = "Thank you. Now you will receive notifications.";
    private static final String REJECT_MESSAGE = "You are not our client, please register.";
    private final UserRepository userRepository;

    @Override
    public String getBotUsername() {
        return BOT_USER_NAME;
    }

    @Override
    public String getBotToken() {
        return TELEGRAM_API_KEY;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        message.setChatId(chatId);
        String userMessage = update.getMessage().getText();
        if (userMessage.equals(START_MESSAGE)) {
            message.setText(WELCOME_MESSAGE);
            try {
                this.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(ERROR_MESSAGE, e);
            }
            return;
        }
        Optional<User> userOptional = userRepository.findByEmail(userMessage);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getChatId() == null) {
                user.setChatId(chatId);
                userRepository.save(user);
                message.setText(INFORM_MESSAGE);
                try {
                    this.execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(ERROR_MESSAGE, e);
                }
            }
        } else {
            message.setText(REJECT_MESSAGE);
            try {
                this.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(ERROR_MESSAGE, e);
            }
        }
    }
}
