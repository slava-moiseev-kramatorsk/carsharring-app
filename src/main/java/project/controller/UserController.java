package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.dto.user.CreateUserDto;
import project.dto.user.UserDto;
import project.model.User;
import project.security.CustomUserDetailService;
import project.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users management",
        description = "End points for CRUD operations with users")
public class UserController {
    private final UserService userService;
    private final CustomUserDetailService customUserDetailService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('MANAGER')or hasRole('CUSTOMER')")
    @Operation(summary = "Get your profile",
            description = "To see your account data")
    public UserDto getMyProfile(Authentication authentication) {
        User user = customUserDetailService.getUserFromAuthentication(authentication);
        return userService.findByEmail(user.getEmail());
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update user role",
            description = "Endpoint for update role and your permission")
    public UserDto updateUserRole(@Positive @PathVariable Long id, String role) {
        return userService.updateUserRole(id, role);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('MANAGER')or hasRole('CUSTOMER')")
    @Operation(summary = "Update your profile",
            description = "Endpoint for update your personal data")
    public UserDto updateYourProfile(Authentication authentication,
                                     @RequestBody @Valid CreateUserDto createUserDto) {
        User user = customUserDetailService.getUserFromAuthentication(authentication);
        return userService.updateYourProfile(user, createUserDto);
    }
}
