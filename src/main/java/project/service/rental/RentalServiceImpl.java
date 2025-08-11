package project.service.rental;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.dto.rental.CreateRentalDto;
import project.dto.rental.RentalDto;
import project.exeption.EntityNotFoundException;
import project.mapper.RentalMapper;
import project.model.Car;
import project.model.Rental;
import project.model.User;
import project.notification.TelegramNotificationsService;
import project.repository.car.CarRepository;
import project.repository.rental.RentalRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final TelegramNotificationsService telegramNotificationsService;

    @Override
    public RentalDto createRental(User user, CreateRentalDto createRentalDto) {
        Car car = carRepository.findById(createRentalDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find car by this id "
                        + createRentalDto.getCarId())
        );
        updateCarInventory(car, -1);
        Rental rental = rentalMapper.toModel(createRentalDto);
        if (createRentalDto.getReturnDate().isBefore(LocalDate.now())) {
            throw new EntityNotFoundException("Return date can't be before today");
        }
        rental.setCar(car);
        rental.setUser(user);
        rental.setActive(true);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(createRentalDto.getReturnDate());
        telegramNotificationsService.sendMessageOfCreateNewRental(user, rental);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public RentalDto getRentalByUserId(User user, Long id) {
        Rental rental = rentalRepository.findByIdAndUserId(user.getId(), id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can`t find rental by this id " + id)
                );
        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalDto setActualReturnDate(User user) {
        Rental rental = rentalRepository.findById(user.getId()).orElseThrow(
                () -> new EntityNotFoundException("rental by this id "
                        + user.getId() + " not exist")
        );
        if (rental.getActualReturnDate() != null) {
            throw new RuntimeException("You are already return car ");
        }
        LocalDate actualReturnDate = LocalDate.now();
        updateCarInventory(rental.getCar(), 1);
        rental.setActualReturnDate(actualReturnDate);
        rental.setActive(false);
        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(updatedRental);
    }

    @Override
    public List<RentalDto> getByUserIdAndIsActive(Long userId, boolean isActive) {
        List<Rental> rentals = rentalRepository.findByUserId(userId);
        rentals = rentals.stream()
                .filter(r -> isExpiredRental(r) == isActive)
                .toList();
        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void checkingForOverdueRent() {
        LocalDate today = LocalDate.now();
        List<Rental> rentals = rentalRepository.findAllByActive(true).stream()
                .filter(r -> r.getActualReturnDate() == null && r.getReturnDate().isAfter(today))
                .toList();
        telegramNotificationsService.sendMessageOfOverdueRentals(rentals);
    }

    private void updateCarInventory(Car car, int inventory) {
        int carInventory = car.getInventory() + inventory;
        if (carInventory < 0) {
            throw new RuntimeException("We can`t give you this car, there are none left");
        }
        car.setInventory(carInventory);
        carRepository.save(car);
    }

    private boolean isExpiredRental(Rental rental) {
        if (rental.getReturnDate() != null) {
            return false;
        }
        LocalDate actualDate = LocalDate.now();
        return !actualDate.isBefore(rental.getRentalDate())
                && !actualDate.isAfter(rental.getReturnDate());
    }
}
