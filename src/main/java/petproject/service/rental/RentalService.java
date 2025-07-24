package petproject.service.rental;

import java.util.List;
import petproject.dto.rental.CreateRentalDto;
import petproject.dto.rental.RentalDto;
import petproject.model.User;

public interface RentalService {
    RentalDto createRental(User user,
                           CreateRentalDto createRentalDto);

    RentalDto getRentalByUserId(User user, Long id);

    RentalDto setActualReturnDate(User user);

    List<RentalDto> getByUserIdAndIsActive(Long userId, boolean isActive);
}
