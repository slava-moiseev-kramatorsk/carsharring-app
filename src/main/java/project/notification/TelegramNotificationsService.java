package project.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.model.Car;
import project.model.Rental;
import project.model.User;
import project.telegram.TelegramBot;

@Service
@RequiredArgsConstructor
public class TelegramNotificationsService implements NotificationService {
    private static final String ERROR_MESSAGE = "Error sending notification to user ";
    private final TelegramBot telegramBot;

    @Override
    public void sendMessageOfCreateNewCar(User user, Car car) {
        Long userChatId = user.getChatId();
        if (userChatId == null) {
            return;
        }
        String textToSendToUser = String
                .format("You have add to DB new car:\n Brand - %s\n Model - %s\n Type - %s\n"
                + "Inventory - %s\n DaileFee - $%.2f",
                        car.getBrand(),
                        car.getModel(),
                        car.getType(),
                        car.getInventory(),
                        car.getDaileFee());
        SendMessage message = new SendMessage();
        message.setChatId(userChatId);
        message.setText(textToSendToUser);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public void sendMessageOfSuccessfulPayment(User user) {
        Long userChatId = user.getChatId();
        if (userChatId == null) {
            return;
        }
        String textTemplateOfSuccessfulPayment = String
                .format("Hello %s, this is a notification of a successful payment",
                        user.getFirstName());
        SendMessage message = new SendMessage();
        message.setChatId(userChatId);
        message.setText(textTemplateOfSuccessfulPayment);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public void sendMessageOfCreateNewRental(User user, Rental rental) {
        Long userChatId = user.getChatId();
        if (userChatId == null) {
            return;
        }
        String textOfCreateNewRental = String
                .format("Hello %s, congratulations on your car rental. "
                        + "You have chosen Brand:%s\n, Model:%s",
                        user.getFirstName(),
                        rental.getCar().getBrand(),
                        rental.getCar().getModel());
        SendMessage message = new SendMessage();
        message.setChatId(userChatId);
        message.setText(textOfCreateNewRental);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }

    @Override
    public void sendMessageOfOverdueRentals(List<Rental> rentalList) {
        for (Rental rental: rentalList) {
            Long chatId = rental.getUser().getChatId();
            if (chatId == null) {
                return;
            }
            String textOfOverdueRental = String
                    .format("Your rent has expired %s", rental.getReturnDate());
            SendMessage message = new SendMessage();
            message.setText(textOfOverdueRental);
            message.setChatId(rental.getUser().getChatId());
            try {
                telegramBot.execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(ERROR_MESSAGE, e);
            }
        }
    }

    @Override
    public void sendMessageOfCanceledPayment(Rental rental, User user) {
        Long userChatId = user.getChatId();
        if (userChatId == null) {
            return;
        }
        String textOfCancelPayment = String
                .format("The rental of your chosen car %s %s has been cancelled.",
                        rental.getCar().getBrand(),
                        rental.getCar().getModel());
        SendMessage message = new SendMessage();
        message.setText(textOfCancelPayment);
        message.setChatId(userChatId);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(ERROR_MESSAGE, e);
        }
    }
}
