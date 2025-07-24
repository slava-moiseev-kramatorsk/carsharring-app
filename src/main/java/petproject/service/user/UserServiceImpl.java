package petproject.service.user;

import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import petproject.dto.user.CreateUserDto;
import petproject.dto.user.UserDto;
import petproject.exeption.RegistrationException;
import petproject.mapper.UserMapper;
import petproject.model.Role;
import petproject.model.User;
import petproject.repository.role.RoleRepository;
import petproject.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new RegistrationException("User with this " + createUserDto.getEmail()
                    + " already exist");
        }
        Role userRole = roleRepository
                .findByRole(Role.RoleName.ROLE_CUSTOMER)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find role: "
                                + Role.RoleName.ROLE_CUSTOMER));
        User user = userMapper.toModel(createUserDto);
        user.setRoles(Set.of(userRole));
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new petproject.exeption.EntityNotFoundException(
                        "Can`t find user by  this email " + email
                )
        );
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUserRole(Long id, String role) {
        Role updatedRole = roleRepository.findByRole(Role.RoleName.valueOf(role)).orElseThrow(
                () -> new petproject.exeption.EntityNotFoundException("Can`t find this role "
                        + role)
        );
        User user = userRepository.findById(id).orElseThrow(
                () -> new petproject.exeption.EntityNotFoundException(
                        "Can`t find user by this id " + id
                )
        );
        user.setRoles(Set.of(updatedRole));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateYourProfile(User user, CreateUserDto createUserDto) {
        user.setEmail(createUserDto.getEmail());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setPassword(createUserDto.getPassword());
        return userMapper.toDto(userRepository.save(user));
    }
}
