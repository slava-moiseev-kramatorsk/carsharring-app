package project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import project.dto.user.CreateUserDto;
import project.dto.user.UserDto;
import project.exeption.EntityNotFoundException;
import project.mapper.UserMapper;
import project.model.Role;
import project.model.User;
import project.repository.role.RoleRepository;
import project.repository.user.UserRepository;
import project.service.user.UserServiceImpl;
import project.service.util.ServiceTestUtil;

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
        Role role = new Role();
        role.setRole(Role.RoleName.ROLE_CUSTOMER);
        UserDto expected = ServiceTestUtil.createUserDtoResponse();
        User user = ServiceTestUtil.createUserForRegisterTest();

        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(roleRepository.findByRole(Role.RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(userMapper.toModel(createUserDto)).thenReturn(user);
        when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserDto actual = userService.register(createUserDto);

        assertEquals(expected, actual);

        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(roleRepository).findByRole(Role.RoleName.ROLE_CUSTOMER);
        verify(userMapper).toModel(createUserDto);
        verify(userMapper).toDto(user);
        verify(userRepository).save(user);
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

        verify(userRepository).findByEmail(createUserDto.getEmail());
    }

    @Test
    @DisplayName("Update user role")
    public void updateUserRole_ok() {
        User userAfterUpdateRole = ServiceTestUtil.createUserForRegisterTest();
        Role roleManager = new Role();
        roleManager.setRole(Role.RoleName.ROLE_MANAGER);
        userAfterUpdateRole.setRoles(Set.of(roleManager));
        UserDto userDto = ServiceTestUtil.createUserDtoResponse();
        User userBeforeUpdateRole = ServiceTestUtil.createUserForRegisterTest();

        when(roleRepository.findByRole(Role.RoleName.ROLE_MANAGER))
                .thenReturn(Optional.of(roleManager));
        when(userRepository.findById(userBeforeUpdateRole.getId()))
                .thenReturn(Optional.of(userBeforeUpdateRole));
        when(userRepository.save(userBeforeUpdateRole)).thenReturn(userAfterUpdateRole);
        when(userMapper.toDto(userAfterUpdateRole)).thenReturn(userDto);

        userService.updateUserRole(userBeforeUpdateRole.getId(), "ROLE_MANAGER");
        assertEquals(userBeforeUpdateRole.getRoles(), userAfterUpdateRole.getRoles());

        verify(roleRepository).findByRole(Role.RoleName.ROLE_MANAGER);
        verify(userRepository).save(userBeforeUpdateRole);
        verify(userRepository).findById(userBeforeUpdateRole.getId());
        verify(userMapper).toDto(userAfterUpdateRole);
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

        verify(userRepository).save(userBeforeUpdateData);
        verify(userMapper).toDto(userAfterUpdateData);
    }
}
