package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.dto.user.CreateUserDto;
import project.dto.user.UserDto;
import project.dto.user.UserLoginDto;
import project.dto.user.UserLoginResponseDto;
import project.security.AuthenticationService;
import project.service.user.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users management", description = "Endpoints for users management")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Add new user", description = "Add new user to DB")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody @Valid CreateUserDto createUserDto) {
        return userService.register(createUserDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginDto userLoginDto) {
        return authenticationService.authenticate(userLoginDto);
    }
}
