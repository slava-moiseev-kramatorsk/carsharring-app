package petproject.validator.car;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import petproject.model.Car;

public class CarTypeValidator implements ConstraintValidator<CarType, String> {

    @Override
    public boolean isValid(String inputCarType, ConstraintValidatorContext context) {
        try {
            Car.Type.valueOf(inputCarType.toUpperCase());
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
