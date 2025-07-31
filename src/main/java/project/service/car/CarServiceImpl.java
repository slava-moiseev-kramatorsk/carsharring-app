package project.service.car;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import project.dto.car.CarDto;
import project.dto.car.CarSearchParams;
import project.dto.car.CreateCarDto;
import project.exeption.EntityNotFoundException;
import project.mapper.CarMapper;
import project.model.Car;
import project.model.User;
import project.notification.NotificationService;
import project.repository.car.CarRepository;
import project.repository.car.CarSpecificationBuilder;
import project.security.CustomUserDetailService;

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
