package project.service.car;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.dto.car.CarDto;
import project.dto.car.CarSearchParams;
import project.dto.car.CreateCarDto;
import project.model.User;

public interface CarService {
    CarDto save(User user,
                CreateCarDto createCarDto);

    List<CarDto> findAll(Pageable pageable);

    CarDto findById(Long id);

    void deleteById(Long id);

    List<CarDto> search(CarSearchParams searchParams);
}
