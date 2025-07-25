package petproject.repository.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import petproject.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findBySessionId(String sessionId);

}
