package petproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import petproject.dto.rental.CreateRentalDto;
import petproject.dto.rental.RentalDto;
import petproject.mapper.RentalMapper;
import petproject.model.Car;
import petproject.model.Rental;
import petproject.model.User;
import petproject.notification.TelegramNotificationsService;
import petproject.repository.car.CarRepository;
import petproject.repository.rental.RentalRepository;
import petproject.service.rental.RentalServiceImpl;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private TelegramNotificationsService telegramNotificationsService;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("Create new rental")
    public void createNewRental_validData_ok() {
        Rental rental = ServiceTestUtil.createTestRental();
        RentalDto expected = ServiceTestUtil.createRentalDto();
        Car car = ServiceTestUtil.createOneCar();
        User user = ServiceTestUtil.createTestUser();
        CreateRentalDto createRentalDto = new CreateRentalDto()
                .setRentalDate(rental.getRentalDate())
                .setReturnDate(rental.getReturnDate())
                .setCarId(rental.getCar().getId());

        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toModel(createRentalDto)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expected);
        when(carRepository.findById(rental.getCar().getId())).thenReturn(Optional.of(car));

        RentalDto actual = rentalService.createRental(user, createRentalDto);

        verify(telegramNotificationsService, times(1))
                .sendMessageOfCreateNewRental(user, rental);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find rental by id")
    public void findRentalById_validData_ok() {
        Long id = 5L;
        Rental rental = ServiceTestUtil.createTestRental();
        RentalDto expected = ServiceTestUtil.createRentalDto();

        when(rentalRepository.findByIdAndUserId(id, rental.getUser().getId()))
                .thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(expected);

        RentalDto actual = rentalService.getRentalByUserId(rental.getUser(), id);

        verify(rentalRepository, times(1))
                .findByIdAndUserId(rental.getUser().getId(), id);
        verify(rentalMapper, times(1)).toDto(rental);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Set actual returnDate - close rental")
    public void setActualReturnDate_ok() {
        Rental rental = ServiceTestUtil.createTestRental();
        rental.setActive(true);
        rental.setActualReturnDate(null);
        RentalDto expected = ServiceTestUtil.createRentalDto()
                .setReturnDate(LocalDate.now().minusDays(3))
                .setRentalDate(LocalDate.now().minusDays(5))
                .setActualReturnDate(LocalDate.now());

        when(rentalRepository.findById(rental.getUser().getId())).thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expected);

        RentalDto actual = rentalService.setActualReturnDate(rental.getUser());

        assertEquals(expected, actual);

        verify(rentalRepository, times(1)).findById(rental.getUser().getId());
        verify(rentalRepository,times(1)).save(rental);
        verify(rentalMapper, times(1)).toDto(rental);
    }
}
