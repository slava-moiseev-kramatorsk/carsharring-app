package petproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import petproject.dto.user.CreateUserDto;
import petproject.dto.user.UserDto;
import petproject.exeption.EntityNotFoundException;
import petproject.mapper.UserMapper;
import petproject.model.Role;
import petproject.model.User;
import petproject.repository.role.RoleRepository;
import petproject.repository.user.UserRepository;
import petproject.service.user.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Create new user")
    public void saveNewUser_validData_ok() {
        CreateUserDto createUserDto = ServiceTestUtil.createUserToRegister();
        UserDto expected = ServiceTestUtil.createUserDtoResponse();
        Role role = new Role(Role.RoleName.ROLE_CUSTOMER);
        User user = ServiceTestUtil.createUserForRegisterTest();

        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(roleRepository.findByRole(Role.RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(userMapper.toModel(createUserDto)).thenReturn(user);
        when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserDto actual = userService.register(createUserDto);

        assertEquals(expected, actual);

        verify(userRepository, times(1)).existsByEmail(createUserDto.getEmail());
        verify(roleRepository, times(1)).findByRole(Role.RoleName.ROLE_CUSTOMER);
        verify(userMapper,times(1)).toModel(createUserDto);
        verify(userMapper, times(1)).toDto(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Get user by invalid email, exception expected")
    public void getUserByInvalidEmail_shouldThrowException() {
        CreateUserDto createUserDto = ServiceTestUtil.createUserToRegister();
        when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.findByEmail(createUserDto.getEmail())
        );
        String expected = "Can`t find user by  this email " + createUserDto.getEmail();
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update user role")
    public void updateUserRole_ok() {
        User userBeforeUpdateRole = ServiceTestUtil.createUserForRegisterTest();
        User userAfterUpdateRole = ServiceTestUtil.createUserForRegisterTest();
        Role roleManager = new Role(Role.RoleName.ROLE_MANAGER);
        userAfterUpdateRole.setRoles(Set.of(roleManager));
        UserDto userDto = ServiceTestUtil.createUserDtoResponse();

        when(roleRepository.findByRole(Role.RoleName.ROLE_MANAGER))
                .thenReturn(Optional.of(roleManager));
        when(userRepository.findById(userBeforeUpdateRole.getId()))
                .thenReturn(Optional.of(userBeforeUpdateRole));
        when(userRepository.save(userBeforeUpdateRole)).thenReturn(userAfterUpdateRole);
        when(userMapper.toDto(userAfterUpdateRole)).thenReturn(userDto);

        userService.updateUserRole(userBeforeUpdateRole.getId(), "ROLE_MANAGER");
        assertEquals(userBeforeUpdateRole.getRoles(), userAfterUpdateRole.getRoles());

        verify(roleRepository, times(1)).findByRole(Role.RoleName.ROLE_MANAGER);
        verify(userRepository, times(1)).save(userBeforeUpdateRole);
        verify(userMapper, times(1)).toDto(userAfterUpdateRole);
    }

    @Test
    @DisplayName("Update user role with invalid role exception expected")
    public void updateRole_invalidData_shouldThrowException() {
        Long id = 1L;
        String invalidRole = "ROLE_CLIENT";

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUserRole(id, invalidRole));
    }

    @Test
    @DisplayName("Update your profile test")
    public void updateUserProfile_validData_ok() {
        User userAfterUpdateData = ServiceTestUtil.createUserForRegisterTest();
        CreateUserDto dataForUpdate = ServiceTestUtil.createUserDtoForUpdate();
        userAfterUpdateData.setPassword(dataForUpdate.getPassword());
        userAfterUpdateData.setEmail(dataForUpdate.getEmail());
        userAfterUpdateData.setFirstName(dataForUpdate.getFirstName());
        userAfterUpdateData.setLastName(dataForUpdate.getLastName());

        UserDto expected = new UserDto()
                .setEmail(dataForUpdate.getEmail())
                .setFirstName(dataForUpdate.getFirstName())
                .setLastName(dataForUpdate.getLastName());
        User userBeforeUpdateData = ServiceTestUtil.createUserForRegisterTest();
        when(userRepository.save(userBeforeUpdateData)).thenReturn(userAfterUpdateData);
        when(userMapper.toDto(userAfterUpdateData)).thenReturn(expected);

        UserDto actual = userService.updateYourProfile(userBeforeUpdateData, dataForUpdate);
        assertEquals(expected, actual);
    }
}
