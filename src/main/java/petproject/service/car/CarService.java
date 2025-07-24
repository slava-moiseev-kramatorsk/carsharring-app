package petproject.service.car;

import java.util.List;
import org.springframework.data.domain.Pageable;
import petproject.dto.car.CarDto;
import petproject.dto.car.CarSearchParams;
import petproject.dto.car.CreateCarDto;
import petproject.model.User;

public interface CarService {
    CarDto save(User user,
                CreateCarDto createCarDto);

    List<CarDto> findAll(Pageable pageable);

    CarDto findById(Long id);

    void deleteById(Long id);

    List<CarDto> search(CarSearchParams searchParams);
}
