package project.dto.car;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import project.validator.car.CarType;
import project.validator.car.ModelAndBrand;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class CreateCarDto {
    @ModelAndBrand
    @NotBlank(message = "Model of car cannot be empty")
    private String model;
    @ModelAndBrand
    @NotBlank(message = "Brand of car cannot be empty")
    private String brand;
    @CarType
    @NotBlank(message = "Car type cannot be empty")
    private String type;
    @Positive
    private int inventory;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal daileFee;
}
