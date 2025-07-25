package petproject.service.user;

import petproject.dto.user.CreateUserDto;
import petproject.dto.user.UserDto;
import petproject.model.User;

public interface UserService {
    UserDto register(CreateUserDto createUserDto);

    UserDto findByEmail(String email);

    UserDto updateUserRole(Long id, String role);

    UserDto updateYourProfile(User user, CreateUserDto createUserDto);
}
