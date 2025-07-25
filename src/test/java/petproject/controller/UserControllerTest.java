package petproject.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import petproject.dto.user.CreateUserDto;
import petproject.dto.user.UserDto;
import petproject.mapper.UserMapper;
import petproject.model.User;
import petproject.repository.user.UserRepository;
import petproject.security.CustomUserDetailService;
import petproject.service.user.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepositoryMockBean;
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private CustomUserDetailService customUserDetailService;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("databases/delete-from-users.sql"));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Get your profile info")
    void getProfileInfo_ok() throws Exception {
        UserDto expected = ControllerTestUtil.createUserDtoForTest();
        User testUser = ControllerTestUtil.createUserForTests();

        when(customUserDetailService.getUserFromAuthentication(any()))
                .thenReturn(testUser);
        when(userService.findByEmail(anyString())).thenReturn(expected);;
        when(userRepositoryMockBean.save(any(User.class)))
                .thenReturn(testUser);
        when(userMapper.toDto(any(User.class)))
                .thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Update your profile info")
    void updateYourProfileData_ok() throws Exception {
        CreateUserDto dtoForUpdate = ControllerTestUtil.userDtoForUpdate();
        User testUser = ControllerTestUtil.createUserForTests();
        UserDto updatedUsersData = new UserDto()
                .setEmail(dtoForUpdate.getEmail())
                        .setFirstName(dtoForUpdate.getFirstName())
                                .setLastName(dtoForUpdate.getLastName());

        when(customUserDetailService.getUserFromAuthentication(any()))
                .thenReturn(testUser);

        when(userRepositoryMockBean.findByEmail(anyString()))
                .thenReturn(Optional.of(testUser));

        when(userService.updateYourProfile(any(User.class), any(CreateUserDto.class)))
                .thenReturn(updatedUsersData);

        when(userMapper.toDto(any(User.class)))
                .thenReturn(updatedUsersData);

        String jsonBody = objectMapper.writeValueAsString(updatedUsersData);

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dtoForUpdate.getEmail()))
                .andExpect(jsonPath("$.firstName").value(dtoForUpdate.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(dtoForUpdate.getLastName()));
    }
}
