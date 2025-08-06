package project.controller.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import project.dto.car.CarDto;
import project.dto.car.CreateCarDto;
import project.dto.payment.PaymentDto;
import project.dto.user.CreateUserDto;
import project.dto.user.UserDto;
import project.model.Car;
import project.model.Payment;
import project.model.Rental;
import project.model.User;

public class ControllerTestUtil {

    public static CreateCarDto createOneCarToRequest() {
        return new CreateCarDto()
                .setBrand("Toyota")
                .setModel("Camry")
                .setType("SEDAN")
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(90));
    }

    public static CarDto createCarDto() {
        return new CarDto()
                .setBrand(createOneCarToRequest().getBrand())
                .setModel(createOneCarToRequest().getModel())
                .setType(Car.Type.valueOf(createOneCarToRequest().getType().toUpperCase()))
                .setInventory(createOneCarToRequest().getInventory())
                .setDaileFee(createOneCarToRequest().getDaileFee());
    }

    public static User createUserForTests() {
        User user = new User();
        user.setId(5L);
        user.setFirstName("Slava");
        user.setLastName("Moiseeev");
        user.setEmail("Admin@gmail.com");
        user.setPassword("password");
        user.setChatId(204343344L);
        return user;
    }

    public static List<CarDto> carDtoListSizeThree() {
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

    public static List<CarDto> carDtoListSizeTwo() {
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

    public static PaymentDto createTestPayment() {
        return new PaymentDto()
                .setStatus(String.valueOf(Payment.Status.PENDING))
                .setType(String.valueOf(Payment.Type.PAYMENT))
                .setRentalId(3L)
                .setSessionId("34523")
                .setAmount(BigDecimal.valueOf(157));
    }

    public static PaymentDto createForUpdateStatusOrCanceling() {
        return new PaymentDto()
                .setStatus(String.valueOf(Payment.Status.PAID))
                .setType(String.valueOf(Payment.Type.FINE))
                .setRentalId(2L)
                .setSessionId("11432")
                .setAmount(BigDecimal.valueOf(120));
    }

    public static Car createOneCarForTest() {
        Car car = new Car();
        car.setBrand("Fiat");
        car.setModel("Typo");
        car.setType(Car.Type.SEDAN);
        car.setInventory(1);
        car.setDaileFee(BigDecimal.valueOf(50));
        return car;
    }

    public static Rental createRentalForTest() {
        Rental rental = new Rental();
        rental.setUser(createUserForTests());
        rental.setRentalDate(LocalDate.parse("2025-01-13"));
        rental.setReturnDate(LocalDate.parse("2024-01-21"));
        rental.setCar(createOneCarForTest());
        return rental;
    }

    public static Payment createOnePayment() {
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(createRentalForTest());
        payment.setSessionUrl("url");
        payment.setSessionId("3785");
        payment.setAmount(BigDecimal.valueOf(170));
        return payment;
    }

    public static UserDto createUserDtoForTest() {
        return new UserDto()
                .setEmail("Admin@gmail.com")
                .setFirstName("Slava")
                .setLastName("Moiseeev");
    }

    public static CreateUserDto userDtoForUpdate() {
        return new CreateUserDto()
                .setEmail("updatedEmail")
                .setFirstName("updatedFirstName")
                .setLastName("updatedLastName")
                .setPassword("updatedPassword123");
    }
}
