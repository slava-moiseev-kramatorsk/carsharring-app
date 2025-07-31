package project.validator.car;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ModelAndBrandValidator implements ConstraintValidator<ModelAndBrand, String> {
    private static final String REGEX_PASSWORD = "^[a-zA-Z0-9\\s-]+$";
    private static final int REGEX_MAX_PASSWORD_LENGTH = 20;

    @Override
    public boolean isValid(
            String inputModel,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        return Pattern.compile(REGEX_PASSWORD)
                .matcher(inputModel).matches()
                && inputModel.length() < REGEX_MAX_PASSWORD_LENGTH;
    }
}
