package petproject.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import petproject.dto.car.CarDto;
import petproject.dto.car.CreateCarDto;
import petproject.dto.payment.PaymentDto;
import petproject.dto.user.CreateUserDto;
import petproject.dto.user.UserDto;
import petproject.model.Car;
import petproject.model.Payment;
import petproject.model.Rental;
import petproject.model.User;

public class ControllerTestUtil {

    static CreateCarDto createOneCarToRequest() {
        return new CreateCarDto()
                .setBrand("Toyota")
                .setModel("Camry")
                .setType("SEDAN")
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(90));
    }

    static CarDto createCarDto() {
        return new CarDto()
                .setBrand(createOneCarToRequest().getBrand())
                .setModel(createOneCarToRequest().getModel())
                .setType(Car.Type.valueOf(createOneCarToRequest().getType().toUpperCase()))
                .setInventory(createOneCarToRequest().getInventory())
                .setDaileFee(createOneCarToRequest().getDaileFee());
    }

    static User createUserForTests() {
        User user = new User();
        user.setId(5L);
        user.setFirstName("Slava");
        user.setLastName("Moiseeev");
        user.setEmail("Admin@gmail.com");
        user.setPassword("password");
        user.setChatId(204343344L);
        return user;
    }

    static List<CarDto> carDtoListSizeThree() {
        List<CarDto> carDtoList = new ArrayList<>();
        carDtoList.add(new CarDto()
                .setBrand("Brand")
                .setModel("Model")
                .setType(Car.Type.SEDAN)
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(100)));
        carDtoList.add(new CarDto().setBrand("Fiat")
                .setModel("Typo")
                .setType(Car.Type.SEDAN)
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(50)));
        carDtoList.add(new CarDto()
                .setModel("Camry")
                .setType(Car.Type.SEDAN)
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(90))
                .setBrand("Toyota"));
        return carDtoList;
    }

    static List<CarDto> carDtoListSizeTwo() {
        List<CarDto> carDtoList = new ArrayList<>();
        carDtoList.add(new CarDto().setBrand("Fiat")
                .setModel("Typo")
                .setType(Car.Type.SEDAN)
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(50)));
        carDtoList.add(new CarDto()
                .setModel("Camry")
                .setType(Car.Type.SEDAN)
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(90))
                .setBrand("Toyota"));
        return carDtoList;
    }

    static PaymentDto createTestPayment() {
        return new PaymentDto()
                .setStatus(String.valueOf(Payment.Status.PENDING))
                .setType(String.valueOf(Payment.Type.PAYMENT))
                .setRentalId(3L)
                .setSessionId("34523")
                .setAmount(BigDecimal.valueOf(157));
    }

    static PaymentDto createForUpdateStatusOrCanceling() {
        return new PaymentDto()
                .setStatus(String.valueOf(Payment.Status.PAID))
                .setType(String.valueOf(Payment.Type.FINE))
                .setRentalId(2L)
                .setSessionId("11432")
                .setAmount(BigDecimal.valueOf(120));
    }

    static Car createOneCarForTest() {
        Car car = new Car();
        car.setBrand("Fiat");
        car.setModel("Typo");
        car.setType(Car.Type.SEDAN);
        car.setInventory(1);
        car.setDaileFee(BigDecimal.valueOf(50));
        return car;
    }

    static Rental createRentalForTest() {
        Rental rental = new Rental();
        rental.setUser(createUserForTests());
        rental.setRentalDate(LocalDate.parse("2025-01-13"));
        rental.setReturnDate(LocalDate.parse("2024-01-21"));
        rental.setCar(createOneCarForTest());
        return rental;
    }

    static Payment createOnePayment() {
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(createRentalForTest());
        payment.setSessionUrl("url");
        payment.setSessionId("3785");
        payment.setAmount(BigDecimal.valueOf(170));
        return payment;
    }

    static UserDto createUserDtoForTest() {
        return new UserDto()
                .setEmail("Admin@gmail.com")
                .setFirstName("Slava")
                .setLastName("Moiseeev");
    }

    static CreateUserDto userDtoForUpdate() {
        return new CreateUserDto()
                .setEmail("updatedEmail")
                .setFirstName("updatedFirstName")
                .setLastName("updatedLastName")
                .setPassword("updatedPassword123");
    }
}
