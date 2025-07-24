package petproject.validator.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    @Override
    public boolean isValid(
            String inputEmail,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        return Pattern.compile(REGEX_EMAIL).matcher(inputEmail).matches();
    }
}
