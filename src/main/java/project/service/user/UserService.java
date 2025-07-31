package project.service.user;

import project.dto.user.CreateUserDto;
import project.dto.user.UserDto;
import project.model.User;

public interface UserService {
    UserDto register(CreateUserDto createUserDto);

    UserDto findByEmail(String email);

    UserDto updateUserRole(Long id, String role);

    UserDto updateYourProfile(User user, CreateUserDto createUserDto);
}
