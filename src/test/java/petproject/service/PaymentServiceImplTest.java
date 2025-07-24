package petproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
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
import petproject.dto.payment.CreatePaymentDto;
import petproject.dto.payment.PaymentDto;
import petproject.mapper.PaymentMapper;
import petproject.model.Payment;
import petproject.model.Rental;
import petproject.model.User;
import petproject.notification.TelegramNotificationsService;
import petproject.repository.payment.PaymentRepository;
import petproject.repository.rental.RentalRepository;
import petproject.service.payment.PaymentServiceImpl;

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
    @DisplayName("Create new payment")
    public void getPaymentByID_validData_ok() {
        Payment payment = ServiceTestUtil.createTestPayment();
        PaymentDto expected = ServiceTestUtil.createPaymentDto();

        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentMapper.toPaymentDto(payment)).thenReturn(expected);

        PaymentDto actual = paymentService.getById(12L);
        assertEquals(expected, actual);
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

        verify(paymentRepository, times(1)).findBySessionId(sessionId);
        verify(paymentRepository, times(1)).save(payment);
        verify(paymentMapper, times(1)).toPaymentDto(payment);
        verify(notificationsService, times(1))
                .sendMessageOfSuccessfulPayment(user);
        assertEquals(Payment.Status.PAID, payment.getStatus());
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
    }
}
