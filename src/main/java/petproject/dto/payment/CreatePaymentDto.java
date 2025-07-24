package petproject.dto.payment;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePaymentDto {
    private Long rentalId;
    private String paymentType;
}
