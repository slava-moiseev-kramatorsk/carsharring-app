package project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import project.dto.payment.CreatePaymentDto;
import project.dto.payment.PaymentDto;
import project.exeption.EntityNotFoundException;
import project.mapper.PaymentMapper;
import project.model.Payment;
import project.model.Rental;
import project.model.User;
import project.notification.TelegramNotificationsService;
import project.repository.payment.PaymentRepository;
import project.repository.rental.RentalRepository;
import project.service.payment.PaymentServiceImpl;
import project.service.util.ServiceTestUtil;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private TelegramNotificationsService notificationsService;
    @Mock
    private RentalRepository rentalRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Get payment by valid id")
    public void getPaymentByID_validData_ok() {
        Payment payment = ServiceTestUtil.createTestPayment();
        PaymentDto expected = ServiceTestUtil.createPaymentDto();

        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentMapper.toPaymentDto(payment)).thenReturn(expected);

        PaymentDto actual = paymentService.getById(12L);
        assertEquals(expected, actual);

        verify(paymentRepository).findById(anyLong());
        verify(paymentMapper).toPaymentDto(payment);
    }

    @Test
    @DisplayName("Get payment by invalid id")
    public void getPayment_WithInvalidId_shouldThrowException() {
        Long id = 100L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.getById(id)
        );
        String expected = "Can`t find payment bi this id " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(paymentRepository).findById(id);
    }

    @Test
    @DisplayName("Update payment status")
    public void updatePaymentStatus_ok() {
        Payment payment = ServiceTestUtil.createTestPayment();
        PaymentDto expected = ServiceTestUtil.createPaymentDto();
        User user = ServiceTestUtil.createTestUser();
        String sessionId = "123456";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(expected);

        PaymentDto actual = paymentService.updatePaymentStatus(sessionId, user);

        verify(paymentRepository).findBySessionId(sessionId);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toPaymentDto(payment);
        verify(notificationsService).sendMessageOfSuccessfulPayment(user);
        assertEquals(Payment.Status.PAID, payment.getStatus());
    }

    @Test
    @DisplayName("Update payment status by invalid session_id")
    public void updatePaymentStatus_invalidSessionId_shouldThrowException() {
        String sessionId = "000000";
        User user = ServiceTestUtil.createTestUser();

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.updatePaymentStatus(sessionId,user)
        );

        String expected = "Can`t find Payment by this sessionId " + sessionId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(paymentRepository).findBySessionId(sessionId);
    }

    @Test
    @DisplayName("Create new payment")
    public void createPayment_ok() {
        Payment payment = ServiceTestUtil.createTestPayment();
        PaymentDto expected = ServiceTestUtil.createPaymentDto();
        Rental rental = ServiceTestUtil.createTestRental();
        CreatePaymentDto createPaymentDto = new CreatePaymentDto()
                .setPaymentType("PAYMENT")
                .setRentalId(1L);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(expected);

        try (MockedStatic<Session> sessionMock = mockStatic(Session.class)) {
            Session mockSession = mock(Session.class);
            when(mockSession.getUrl()).thenReturn("url");
            when(mockSession.getId()).thenReturn("34324");

            sessionMock.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            PaymentDto actual = paymentService.createPayment(createPaymentDto);
            assertEquals(expected, actual);
        }
        verify(rentalRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toPaymentDto(payment);
    }
}
