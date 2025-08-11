package project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.config.MapperConfig;
import project.dto.rental.CreateRentalDto;
import project.dto.rental.RentalDto;
import project.model.Rental;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(source = "carId", target = "car.id")
    Rental toModel(CreateRentalDto createRentalDto);

    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "user.id", target = "userId")
    RentalDto toDto(Rental rental);
}
