package project.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.assertj.core.util.BigDecimalComparator;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import project.controller.util.ControllerTestUtil;
import project.dto.car.CarDto;
import project.dto.car.CreateCarDto;
import project.model.Car;
import project.model.User;
import project.notification.TelegramNotificationsService;
import project.repository.user.UserRepository;
import project.security.CustomUserDetailService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
    protected static MockMvc mockMvc;
    private static final String DELETE_FROM_CARS = "classpath:databases/delete-from-cars.sql";
    private static final String ADD_THREE_CARS = "classpath:databases/add-three-cars.sql";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @MockBean
    private TelegramNotificationsService notificationsService;
    @MockBean
    private UserRepository userRepositoryMockBean;

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
                    new ClassPathResource("databases/delete-from-cars.sql"));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = DELETE_FROM_CARS,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DELETE_FROM_CARS,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new car")
    void createCar_validData_Success() throws Exception {
        CreateCarDto carToRequest = ControllerTestUtil.createOneCarToRequest();
        CarDto expected = ControllerTestUtil.createCarDto();

        User testUser = ControllerTestUtil.createUserForTests();
        when(userRepositoryMockBean.findByEmail(anyString()))
                .thenReturn(Optional.of(testUser));

        String jsonRequest = objectMapper.writeValueAsString(carToRequest);

        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(notificationsService)
                .sendMessageOfCreateNewCar(any(User.class), any(Car.class));

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Add car with empty data")
    public void caveCar_invalidData_expectedStatusBadRequest() throws Exception {
        CreateCarDto invalidCarDto = new CreateCarDto()
                .setBrand("")
                .setModel("")
                .setInventory(-1)
                .setType("")
                .setDaileFee(BigDecimal.valueOf(-0.66));
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCarDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CUSTOMER"})
    @Sql(scripts = {DELETE_FROM_CARS, ADD_THREE_CARS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DELETE_FROM_CARS,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get all cars from DB")
    void getAllCar_Success() throws Exception {
        List<CarDto> expectedList = ControllerTestUtil.carDtoListSizeThree();

        MvcResult result = mockMvc.perform(
                        get("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        JsonNode rootNode = objectMapper.readTree(result.getResponse().getContentAsByteArray());

        List<CarDto> actualList = objectMapper.convertValue(
                rootNode.get("content"),
                new TypeReference<>() {
                }
        );
        assertEquals(expectedList.size(), actualList.size());
        assertThat(actualList)
                .usingRecursiveComparison()
                .withComparatorForType(new BigDecimalComparator(), BigDecimal.class)
                .ignoringFields("id")
                .isEqualTo(expectedList);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {DELETE_FROM_CARS, ADD_THREE_CARS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DELETE_FROM_CARS,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get car by id")
    void getCarById_Ok() throws Exception {
        Long id = 3L;
        CarDto expected = ControllerTestUtil.createCarDto();

        MvcResult result = mockMvc.perform(get("/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CarDto.class);
        assertEquals(actual, expected);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {DELETE_FROM_CARS,ADD_THREE_CARS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = DELETE_FROM_CARS,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCarById_changeSizeDB_Ok() throws Exception {
        Long id = 1L;
        List<CarDto> expected = ControllerTestUtil.carDtoListSizeTwo();

        mockMvc.perform(
                        delete("/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(
                        get("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<CarDto> actual = objectMapper.convertValue(
                root.get("content"),
                new TypeReference<List<CarDto>>() {}
        );

        assertEquals(expected.size(), actual.size());
        assertEquals(expected,actual);
    }
}
