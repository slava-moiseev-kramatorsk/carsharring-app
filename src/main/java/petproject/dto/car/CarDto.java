package petproject.dto.car;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import petproject.model.Car;

@Data
@Accessors(chain = true)
public class CarDto {
    private String model;
    private String brand;
    private Car.Type type;
    private int inventory;
    private BigDecimal daileFee;
}
