package petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import petproject.dto.user.CreateUserDto;
import petproject.dto.user.UserDto;
import petproject.dto.user.UserLoginDto;
import petproject.dto.user.UserLoginResponseDto;
import petproject.security.AuthenticationService;
import petproject.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Users management", description = "Endpoints for users management")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Add new user", description = "Add new user to DB")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody CreateUserDto createUserDto) {
        return userService.register(createUserDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginDto userLoginDto) {
        return authenticationService.authenticate(userLoginDto);
    }
}
