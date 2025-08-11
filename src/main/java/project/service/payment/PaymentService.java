package project.service.payment;

import project.dto.payment.CreatePaymentDto;
import project.dto.payment.PaymentDto;
import project.model.User;

public interface PaymentService {

    PaymentDto createPayment(CreatePaymentDto createPaymentDto);

    PaymentDto getById(Long id);

    PaymentDto updatePaymentStatus(String sessionId, User user);

    void paymentCancel(String sessionId, User user);
}
