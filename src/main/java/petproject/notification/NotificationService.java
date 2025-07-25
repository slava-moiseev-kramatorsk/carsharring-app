package petproject.notification;

import java.util.List;
import petproject.model.Car;
import petproject.model.Rental;
import petproject.model.User;

public interface NotificationService {
    void sendMessageOfCreateNewCar(User user, Car car);

    void sendMessageOfSuccessfulPayment(User user);

    void sendMessageOfCreateNewRental(User user, Rental rental);

    void sendMessageOfOverdueRentals(List<Rental> rentalList);

    void sendMessageOfCanceledPayment(Rental rental, User user);
}
