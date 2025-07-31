package project.mapper;

import org.mapstruct.Mapper;
import project.config.MapperConfig;
import project.dto.car.CarDto;
import project.dto.car.CreateCarDto;
import project.model.Car;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toModel(CreateCarDto createCarDto);
}
