package petproject.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import petproject.dto.car.CarDto;
import petproject.dto.car.CreateCarDto;
import petproject.exeption.EntityNotFoundException;
import petproject.mapper.CarMapper;
import petproject.model.Car;
import petproject.model.User;
import petproject.notification.TelegramNotificationsService;
import petproject.repository.car.CarRepository;
import petproject.service.car.CarServiceImpl;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private TelegramNotificationsService notificationsService;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Add one car to DB")
    public void saveCar_ValidData_ok() {
        CreateCarDto createCarDto = ServiceTestUtil.createCarDto();
        Car testCar = ServiceTestUtil.createOneCar();
        CarDto expected = ServiceTestUtil.createOneCarDto();
        User testUser = ServiceTestUtil.createTestUser();

        when(carMapper.toModel(createCarDto)).thenReturn(testCar);
        when(carRepository.save(testCar)).thenReturn(testCar);
        when(carMapper.toDto(testCar)).thenReturn(expected);

        CarDto actual = carService.save(testUser, createCarDto);
        verify(notificationsService, times(1))
                .sendMessageOfCreateNewCar(any(User.class), any(Car.class));
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all cars from DB")
    public void getAllCar_ok() {
        List<Car> carsList = ServiceTestUtil.generateThreeCars();
        List<CarDto> carsDtoList = ServiceTestUtil.generateThreeCarDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> page = new PageImpl<>(carsList, pageable, carsList.size());

        when(carRepository.findAll(pageable)).thenReturn(page);
        for (int i = 0; i < carsList.size(); i++) {
            when(carMapper.toDto(carsList.get(i))).thenReturn(carsDtoList.get(i));
        }

        List<CarDto> actual = carService.findAll(pageable);
        verify(carRepository, times(1)).findAll(pageable);
        assertEquals(carsDtoList, actual);
    }

    @Test
    @DisplayName("Get car by id")
    public void getCarById_validData_ok() {
        Car car = ServiceTestUtil.createOneCar();
        CarDto expected = new CarDto()
                .setBrand(car.getBrand())
                .setModel(car.getModel())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDaileFee(car.getDaileFee());

        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(expected);

        CarDto actual = carService.findById(15L);
        verify(carRepository, times(1)).findById(15L);
        verify(carMapper, times(1)).toDto(car);
        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("Get by wrong id, should throw exception")
    public void getCarById_invalidData_notOk() {
        Long invalidId = -1L;

        when(carRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.findById(invalidId)
        );
        String expected = "Can`t find car by id " + invalidId;
        String actual = exception.getMessage();

        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("Delete car by id")
    public void deleteCarById_validIData_ok() {
        Long id = 1L;
        carService.deleteById(id);
        verify(carRepository, times(1)).deleteById(id);
    }
}
