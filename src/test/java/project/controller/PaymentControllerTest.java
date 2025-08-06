package project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import project.controller.util.ControllerTestUtil;
import project.dto.payment.PaymentDto;
import project.model.Payment;
import project.model.Rental;
import project.model.User;
import project.notification.TelegramNotificationsService;
import project.repository.payment.PaymentRepository;
import project.repository.rental.RentalRepository;
import project.repository.user.UserRepository;
import project.service.payment.PaymentService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static final String ADD_THREE_USERS = "classpath:databases/add-three-users.sql";
    private static final String ADD_THREE_CARS = "classpath:databases/add-three-cars.sql";
    private static final String ADD_FIVE_RENTALS = "classpath:databases/add-five-rentals.sql";
    private static final String ADD_THREE_PAYMENTS = "classpath:databases/add-three-payments.sql";
    private static final String DELETE_FROM_CARS = "classpath:databases/delete-from-cars.sql";
    private static final String DELETE_FROM_USERS = "classpath:databases/delete-from-users.sql";
    private static final String DELETE_FROM_RENTALS = "classpath:databases/delete-from-rental.sql";
    private static final String DELETE_FROM_PAYMENTS =
            "classpath:databases/delete-from-payments.sql";

    @MockBean
    private TelegramNotificationsService notificationsService;
    @MockBean
    private UserRepository userRepositoryMockBean;
    @Mock
    private PaymentRepository paymentRepository;
    @MockBean
    private RentalRepository rentalRepositoryMockBean;
    @SpyBean
    private PaymentService paymentServiceMockBean;
    @Autowired
    private ObjectMapper objectMapper;

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
                    new ClassPathResource("databases/delete-from-payments.sql"));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {ADD_THREE_USERS, ADD_THREE_CARS, ADD_FIVE_RENTALS, ADD_THREE_PAYMENTS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {DELETE_FROM_CARS, DELETE_FROM_USERS, DELETE_FROM_RENTALS, DELETE_FROM_PAYMENTS},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get payment by id")
    void getPaymentById_ok() throws Exception {
        Long id = 3L;
        PaymentDto expected = ControllerTestUtil.createTestPayment();

        MvcResult result = mockMvc.perform(get("/payments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentDto.class);
        assertEquals(actual, expected);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {ADD_THREE_USERS, ADD_THREE_CARS, ADD_FIVE_RENTALS, ADD_THREE_PAYMENTS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {DELETE_FROM_CARS, DELETE_FROM_USERS, DELETE_FROM_RENTALS, DELETE_FROM_PAYMENTS},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update payment status")
    void updatePaymentStatus_ok() throws Exception {
        PaymentDto expected = ControllerTestUtil.createForUpdateStatusOrCanceling();
        User testUser = ControllerTestUtil.createUserForTests();

        when(userRepositoryMockBean.findByEmail(anyString()))
                .thenReturn(Optional.of(testUser));
;
        doReturn(expected)
                .when(paymentServiceMockBean)
                .updatePaymentStatus(expected.getSessionId(), testUser);

        MvcResult result = mockMvc.perform(
                        post("/payments/success")
                                .param("session_id", expected.getSessionId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        PaymentDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentDto.class);

        verify(paymentServiceMockBean)
                .updatePaymentStatus(expected.getSessionId(), testUser);

        assertEquals(actual, expected);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CUSTOMER"})
    @Sql(scripts = {ADD_THREE_USERS, ADD_THREE_CARS, ADD_FIVE_RENTALS, ADD_THREE_PAYMENTS},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {DELETE_FROM_CARS, DELETE_FROM_USERS, DELETE_FROM_RENTALS, DELETE_FROM_PAYMENTS},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Cancel payment")
    void cancelPayment_ok() throws Exception {
        User testUser = ControllerTestUtil.createUserForTests();
        Payment testPayment = ControllerTestUtil.createOnePayment();
        Rental testRental = testPayment.getRental();

        when(userRepositoryMockBean.findByEmail(anyString()))
                .thenReturn(Optional.of(testUser));
        when(rentalRepositoryMockBean.findById(anyLong()))
                .thenReturn(Optional.of(testRental));
        when(paymentRepository.findBySessionId(anyString()))
                .thenReturn(Optional.of(testPayment));

        mockMvc.perform(
                post("/payments/cancel")
                        .param("session_id", testPayment.getSessionId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(paymentServiceMockBean)
                .paymentCancel(testPayment.getSessionId(), testUser);
        verify(notificationsService)
                .sendMessageOfCanceledPayment(testRental, testUser);
    }
}
