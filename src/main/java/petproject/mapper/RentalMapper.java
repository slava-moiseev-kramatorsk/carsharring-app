package petproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import petproject.config.MapperConfig;
import petproject.dto.rental.CreateRentalDto;
import petproject.dto.rental.RentalDto;
import petproject.model.Rental;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(source = "carId", target = "car.id")
    Rental toModel(CreateRentalDto createRentalDto);

    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "user.id", target = "userId")
    RentalDto toDto(Rental rental);
}
