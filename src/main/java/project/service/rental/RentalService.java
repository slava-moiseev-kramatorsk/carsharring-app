package project.service.rental;

import java.util.List;
import project.dto.rental.CreateRentalDto;
import project.dto.rental.RentalDto;
import project.model.User;

public interface RentalService {
    RentalDto createRental(User user,
                           CreateRentalDto createRentalDto);

    RentalDto getRentalByUserId(User user, Long id);

    RentalDto setActualReturnDate(User user);

    List<RentalDto> getByUserIdAndIsActive(Long userId, boolean isActive);
}
