package petproject.dto.payment;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentDto {
    private String status;
    private String type;
    private Long rentalId;
    private String sessionId;
    private BigDecimal amount;
}
