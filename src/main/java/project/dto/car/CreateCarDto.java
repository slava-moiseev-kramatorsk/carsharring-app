package project.dto.car;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
    private String model;
    @ModelAndBrand
    private String brand;
    @CarType
    private String type;
    @Min(0)
    private int inventory;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal daileFee;
}
