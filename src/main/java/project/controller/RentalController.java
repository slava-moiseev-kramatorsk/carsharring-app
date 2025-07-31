package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.dto.rental.CreateRentalDto;
import project.dto.rental.RentalDto;
import project.model.User;
import project.security.CustomUserDetailService;
import project.service.rental.RentalService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rentals")
@Tag(name = "Rental management",
        description = "Handles all operations related to car rentals")
public class RentalController {
    private final RentalService rentalService;
    private final CustomUserDetailService customUserDetailService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')or hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new rental",
            description = "Add new car rental")
    public RentalDto createRental(@RequestBody @Valid CreateRentalDto createRentalDto,
                                  Authentication authentication) {
        User user = customUserDetailService.getUserFromAuthentication(authentication);
        return rentalService.createRental(user, createRentalDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Find by user id",
            description = "Get rental by customer id and rental id")
    public RentalDto findByIdAndUserId(Authentication authentication,
                                       @Positive @PathVariable Long id) {
        User user = customUserDetailService.getUserFromAuthentication(authentication);
        return rentalService.getRentalByUserId(user, id);
    }

    @GetMapping("/isActive")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Sort rentals by active",
            description = "Get rentals by status 'active' or 'overdue'")
    public List<RentalDto> getByUserIdAdnIsActive(
            @RequestParam Long userId,
            @RequestParam boolean isActive) {
        return rentalService.getByUserIdAndIsActive(userId, isActive);
    }

    @PostMapping("/return")
    @PreAuthorize("hasRole('MANAGER')or hasRole('CUSTOMER')")
    @Operation(summary = "Endpoint for end of car rental",
            description = "Set actual return date car, close rental")
    public RentalDto setCloseRental(Authentication authentication) {
        User user = customUserDetailService.getUserFromAuthentication(authentication);
        return rentalService.setActualReturnDate(user);
    }
}
