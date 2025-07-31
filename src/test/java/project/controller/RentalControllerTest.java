package project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import project.dto.rental.RentalDto;
import project.model.User;
import project.repository.user.UserRepository;
import project.service.rental.RentalService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerTest {
    protected static MockMvc mockMvc;
    private static final String ADD_THREE_USERS = "classpath:databases/add-three-users.sql";
    private static final String ADD_THREE_CARS = "classpath:databases/add-three-cars.sql";
    private static final String ADD_FIVE_RENTALS = "classpath:databases/add-five-rentals.sql";
    private static final String DELETE_FROM_CARS = "classpath:databases/delete-from-cars.sql";
    private static final String DELETE_FROM_USERS = "classpath:databases/delete-from-users.sql";
    private static final String DELETE_FROM_RENTALS = "classpath:databases/delete-from-rental.sql";
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepositoryMockBean;

    @SpyBean
    private RentalService rentalService;

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
                    new ClassPathResource("databases/delete-from-rental.sql"));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {ADD_THREE_USERS, ADD_THREE_CARS, ADD_FIVE_RENTALS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {DELETE_FROM_CARS, DELETE_FROM_USERS, DELETE_FROM_RENTALS},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get rentals by status")
     void getRentalsByStatus() throws Exception {
        Long id = 1L;
        boolean isActive = false;

        MvcResult result = mockMvc.perform(get("/rentals/isActive")
                        .param("userId", "5")
                        .param("isActive", "false"))
                .andExpect(status().isOk())
                .andReturn();
        List<RentalDto> actual = Arrays.stream(
                        objectMapper.readValue(result.getResponse()
                                .getContentAsString(), RentalDto[].class))
                .toList();

        assertEquals(3, actual.size());
        for (RentalDto rentalDto: actual) {
            assertThat(rentalDto.getUserId()).isIn(5L);
            assertThat(rentalDto.getCarId()).isIn(2L,3L);
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {ADD_THREE_USERS, ADD_THREE_CARS, ADD_FIVE_RENTALS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {DELETE_FROM_CARS, DELETE_FROM_USERS, DELETE_FROM_RENTALS},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get rentals by userId")
    void getRentalById_WithValidId_Ok() throws Exception {
        User testUser = ControllerTestUtil.createUserForTests();

        when(userRepositoryMockBean.findByEmail(anyString()))
                .thenReturn(Optional.of(testUser));

        RentalDto expected = new RentalDto()
                .setCarId(3L)
                .setRentalDate(LocalDate.parse("2025-09-01"))
                .setReturnDate(LocalDate.parse("2025-09-04"))
                .setUserId(5L);

        MvcResult result = mockMvc.perform(get("/rentals/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        RentalDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), RentalDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {ADD_THREE_USERS, ADD_THREE_CARS, ADD_FIVE_RENTALS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {DELETE_FROM_CARS, DELETE_FROM_USERS, DELETE_FROM_RENTALS},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get rentals by status")
    void closeRental_Ok() throws Exception {
        User testUser = ControllerTestUtil.createUserForTests();
        RentalDto expected = new RentalDto()
                .setCarId(3L)
                .setRentalDate(LocalDate.parse("2025-09-01"))
                .setReturnDate(LocalDate.parse("2025-09-04"))
                .setUserId(5L)
                .setActualReturnDate(LocalDate.now());

        when(userRepositoryMockBean.findByEmail(anyString()))
                .thenReturn(Optional.of(testUser));

        doReturn(expected)
                .when(rentalService)
                .setActualReturnDate(testUser);

        MvcResult result = mockMvc.perform(post("/rentals/return"))
                .andExpect(status().isOk())
                .andReturn();

        RentalDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), RentalDto.class);

        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
        verify(rentalService).setActualReturnDate(testUser);
    }
}
