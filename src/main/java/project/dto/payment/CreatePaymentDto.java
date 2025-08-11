package project.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePaymentDto {
    @Positive
    private Long rentalId;
    @NotBlank(message = "Payment Type cannot be empty")
    private String paymentType;
}
