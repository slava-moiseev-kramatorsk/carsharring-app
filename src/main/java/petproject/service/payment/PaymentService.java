package petproject.service.payment;

import petproject.dto.payment.CreatePaymentDto;
import petproject.dto.payment.PaymentDto;
import petproject.model.User;

public interface PaymentService {

    PaymentDto createPayment(CreatePaymentDto createPaymentDto);

    PaymentDto getById(Long id);

    PaymentDto updatePaymentStatus(String sessionId, User user);

    void paymentCancel(String sessionId, User user);
}
