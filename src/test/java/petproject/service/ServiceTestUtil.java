package petproject.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import petproject.dto.car.CarDto;
import petproject.dto.car.CreateCarDto;
import petproject.dto.payment.PaymentDto;
import petproject.dto.rental.RentalDto;
import petproject.dto.user.CreateUserDto;
import petproject.dto.user.UserDto;
import petproject.model.Car;
import petproject.model.Payment;
import petproject.model.Rental;
import petproject.model.Role;
import petproject.model.User;

public class ServiceTestUtil {

    static User createTestUser() {
        User user = new User();
        user.setId(5L);
        user.setFirstName("Slava");
        user.setLastName("Moiseeev");
        user.setEmail("Admin@gmail.com");
        user.setPassword("password");
        user.setChatId(204343344L);
        return user;
    }

    static CreateCarDto createCarDto() {
        return new CreateCarDto()
                .setBrand("Skoda")
                .setModel("Fabia")
                .setType("SEDAN")
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(77));
    }

    static Car createOneCar() {
        Car car = new Car();
        car.setId(15L);
        car.setBrand("Skoda");
        car.setModel("Fabia");
        car.setType(Car.Type.SEDAN);
        car.setInventory(1);
        car.setDaileFee(BigDecimal.valueOf(77));
        return car;
    }

    static CarDto createOneCarDto() {
        return new CarDto()
                .setBrand("Skoda")
                .setModel("Fabia")
                .setType(Car.Type.SEDAN)
                .setInventory(1)
                .setDaileFee(BigDecimal.valueOf(77));
    }

    static List<Car> generateThreeCars() {
        Car car1 = new Car();
        car1.setBrand("Skoda");
        car1.setModel("Octavia");
        car1.setType(Car.Type.HATCHBACK);
        car1.setInventory(1);
        car1.setDaileFee(BigDecimal.valueOf(56));

        Car car2 = new Car();
        car2.setBrand("Tesla");
        car2.setModel("3");
        car2.setType(Car.Type.SEDAN);
        car2.setInventory(2);
        car2.setDaileFee(BigDecimal.valueOf(88));

        Car car3 = new Car();
        car3.setBrand("KIA");
        car3.setModel("Roi");
        car3.setType(Car.Type.SEDAN);
        car3.setInventory(5);
        car3.setDaileFee(BigDecimal.valueOf(44));

        List<Car> list = new ArrayList<>();
        list.add(car1);
        list.add(car2);
        list.add(car3);
        return list;
    }

    static List<CarDto> generateThreeCarDto() {
        CarDto car1 = new CarDto();
        car1.setBrand("Skoda");
        car1.setModel("Octavia");
        car1.setType(Car.Type.HATCHBACK);
        car1.setInventory(1);
        car1.setDaileFee(BigDecimal.valueOf(56));

        CarDto car2 = new CarDto();
        car2.setBrand("Tesla");
        car2.setModel("3");
        car2.setType(Car.Type.SEDAN);
        car2.setInventory(2);
        car2.setDaileFee(BigDecimal.valueOf(88));

        CarDto car3 = new CarDto();
        car3.setBrand("KIA");
        car3.setModel("Roi");
        car3.setType(Car.Type.SEDAN);
        car3.setInventory(5);
        car3.setDaileFee(BigDecimal.valueOf(44));

        List<CarDto> list = new ArrayList<>();
        list.add(car1);
        list.add(car2);
        list.add(car3);
        return list;
    }

    static Payment createTestPayment() {
        Payment payment = new Payment();
        payment.setId(12L);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);
        payment.setRental(createTestRental());
        payment.setSessionUrl("url");
        payment.setSessionId("34324");
        payment.setAmount(BigDecimal.valueOf(66));
        return payment;
    }

    static Rental createTestRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setUser(createTestUser());
        rental.setRentalDate(LocalDate.now().plusDays(1));
        rental.setReturnDate(LocalDate.now().plusDays(3));
        rental.setActualReturnDate(LocalDate.now().plusDays(3));
        rental.setCar(createOneCar());
        return rental;
    }

    static PaymentDto createPaymentDto() {
        return new PaymentDto()
                .setStatus(String.valueOf(Payment.Status.PENDING))
                .setType(String.valueOf(Payment.Type.PAYMENT))
                .setRentalId(1L)
                .setSessionId("34324")
                .setAmount(BigDecimal.valueOf(66));
    }

    static RentalDto createRentalDto() {
        return new RentalDto()
                .setCarId(createOneCar().getId())
                .setUserId(createTestUser().getId())
                .setRentalDate(LocalDate.now().plusDays(1))
                .setReturnDate(LocalDate.now().plusDays(4));
    }

    static CreateUserDto createUserToRegister() {
        return new CreateUserDto()
                .setEmail("Test@gmail.com")
                .setFirstName("TestName")
                .setLastName("TestLastName")
                .setPassword("qwerty123");
    }

    static UserDto createUserDtoResponse() {
        return new UserDto()
                .setEmail("Test@gmail.com")
                .setFirstName("TestName")
                .setLastName("TestLastName");
    }

    static User createUserForRegisterTest() {
        User user = new User();
        user.setEmail("Test@gmail.com");
        user.setFirstName("TestName");
        user.setLastName("TestLastName");
        user.setPassword("qwerty123");
        Role role = new Role(Role.RoleName.ROLE_CUSTOMER);
        user.setRoles(Set.of(role));
        user.setDeleted(false);
        return user;
    }

    static CreateUserDto createUserDtoForUpdate() {
        return new CreateUserDto()
                .setEmail("second@gmailcom")
                .setFirstName("updatedName")
                .setLastName("updatedLastName")
                .setPassword("updatedPassword123");
    }
}
