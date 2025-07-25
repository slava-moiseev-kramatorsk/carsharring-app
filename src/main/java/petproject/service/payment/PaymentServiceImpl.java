package petproject.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import petproject.dto.payment.CreatePaymentDto;
import petproject.dto.payment.PaymentDto;
import petproject.exeption.EntityNotFoundException;
import petproject.mapper.PaymentMapper;
import petproject.model.Payment;
import petproject.model.Rental;
import petproject.model.User;
import petproject.notification.TelegramNotificationsService;
import petproject.repository.payment.PaymentRepository;
import petproject.repository.rental.RentalRepository;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String STRIPE_SECRET_KEY = Dotenv.load().get("STRIPE_SECRET_KEY");
    private static final String SUCCESS_URL = "http://localhost:8080/payments/success?session_id={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL = "http://localhost:8080/payments/cancel?session_id={CHECKOUT_SESSION_ID}";

    static {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;
    private final TelegramNotificationsService telegramNotificationsService;

    @Override
    public PaymentDto createPayment(CreatePaymentDto createPaymentDto) {
        Rental rental = getRentalById(createPaymentDto);
        BigDecimal amount = calculateAmount(rental);
        Payment.Type paymentType = Payment.Type.valueOf(createPaymentDto.getPaymentType());

        SessionCreateParams sessionCreateParams = createSession(amount, paymentType);
        Session session = null;
        try {
            session = Session.create(sessionCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException("Can`t create Stripe Session!");
        }
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.valueOf(createPaymentDto.getPaymentType()));
        payment.setAmount(amount);
        payment.setRental(rental);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        return paymentMapper.toPaymentDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentDto getById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment bi this id " + id));
        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    public PaymentDto updatePaymentStatus(String sessionId, User user) {
        Optional<Payment> paymentOptional = paymentRepository.findBySessionId(sessionId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus(Payment.Status.PAID);
            telegramNotificationsService.sendMessageOfSuccessfulPayment(user);
            return paymentMapper.toPaymentDto(paymentRepository.save(payment));
        } else {
            throw new EntityNotFoundException("Can`t find Payment by this sessionId " + sessionId);
        }
    }

    @Override
    public void paymentCancel(String sessionId, User user) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment by this sessionId "
                + sessionId)
        );
        Rental rental = rentalRepository.findById(payment.getRental().getId()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find rental by this id "
                        + payment.getRental().getId())
        );
        if (payment.getStatus().equals(Payment.Status.PENDING)) {
            telegramNotificationsService.sendMessageOfCanceledPayment(rental, user);
        }
    }

    private Rental getRentalById(CreatePaymentDto createPaymentDto) {
        return rentalRepository.findById(createPaymentDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Can`t find rental with id "
                        + createPaymentDto.getRentalId()));
    }

    private BigDecimal calculateAmount(Rental rental) {
        long days = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getActualReturnDate());
        return rental.getCar().getDaileFee().multiply(BigDecimal.valueOf(days));
    }

    private SessionCreateParams createSession(BigDecimal amount, Payment.Type type) {
        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addLineItem(
                        SessionCreateParams
                                .LineItem
                                .builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmountDecimal(amount
                                                        .multiply(BigDecimal.valueOf(100)))
                                                .setProductData(SessionCreateParams
                                                        .LineItem
                                                        .PriceData
                                                        .ProductData
                                                        .builder()
                                                        .setName("Car Rental (" + type + ")")
                                                        .build()
                                                )
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                ).build();
    }
}
