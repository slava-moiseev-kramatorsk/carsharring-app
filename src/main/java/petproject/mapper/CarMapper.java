package petproject.mapper;

import org.mapstruct.Mapper;
import petproject.config.MapperConfig;
import petproject.dto.car.CarDto;
import petproject.dto.car.CreateCarDto;
import petproject.model.Car;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toModel(CreateCarDto createCarDto);
}
