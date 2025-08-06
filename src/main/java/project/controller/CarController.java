package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.dto.car.CarDto;
import project.dto.car.CarSearchParams;
import project.dto.car.CreateCarDto;
import project.model.User;
import project.security.CustomUserDetailService;
import project.service.car.CarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
@Tag(name = "Carsharring management",
        description = "Endpoints for carsharring management")
public class CarController {
    private final CarService carService;
    private final CustomUserDetailService customUserDetailService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Add new car", description = "Add new car to DB")
    public CarDto save(@RequestBody @Valid CreateCarDto createCarDto,
                       Authentication authentication) {
        User user = customUserDetailService.getUserFromAuthentication(authentication);
        return carService.save(user, createCarDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')or hasRole('CUSTOMER')")
    @Operation(summary = "Get all cars",
            description = "Get all cars from DB")
    public Page<CarDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')or hasRole('CUSTOMER')")
    @Operation(summary = "Get car by id",
            description = "Get singe car by id")
    public CarDto findById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete car by id",
            description = "Delete singe car by id from DB")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteById(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('CUSTOMER')or hasRole('MANAGER')")
    @Operation(summary = "Find car by specific parameters",
            description = "Find car by specific parameters")
    public List<CarDto> search(CarSearchParams searchParams) {
        return carService.search(searchParams);
    }
}
