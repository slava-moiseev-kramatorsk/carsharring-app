package petproject.service.car;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import petproject.dto.car.CarDto;
import petproject.dto.car.CarSearchParams;
import petproject.dto.car.CreateCarDto;
import petproject.exeption.EntityNotFoundException;
import petproject.mapper.CarMapper;
import petproject.model.Car;
import petproject.model.User;
import petproject.notification.NotificationService;
import petproject.repository.car.CarRepository;
import petproject.repository.car.CarSpecificationBuilder;
import petproject.security.CustomUserDetailService;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final CarSpecificationBuilder carSpecificationBuilder;
    private final NotificationService notificationService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public CarDto save(User user,
                       CreateCarDto createCarDto) {
        Car car = carMapper.toModel(createCarDto);
        if (user.getChatId() != null) {
            notificationService.sendMessageOfCreateNewCar(user, car);
        }
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream().map(carMapper::toDto).toList();
    }

    @Override
    public CarDto findById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find car by id " + id)
        );
        return carMapper.toDto(car);
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public List<CarDto> search(CarSearchParams searchParams) {
        Specification<Car> carSpecification = carSpecificationBuilder.build(searchParams);
        return carRepository.findAll(carSpecification).stream()
                .map(carMapper::toDto)
                .toList();
    }
}
