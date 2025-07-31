package project.validator.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String REGEX_PASSWORD = "^[a-zA-Z0-9]+$";
    private static final int REGEX_MIN_PASSWORD_LENGTH = 5;
    private static final int REGEX_MAX_PASSWORD_LENGTH = 20;

    @Override
    public boolean isValid(
            String inputPassword,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        return Pattern.compile(REGEX_PASSWORD)
                .matcher(inputPassword).matches()
                && (inputPassword.length() > REGEX_MIN_PASSWORD_LENGTH
                && inputPassword.length() < REGEX_MAX_PASSWORD_LENGTH);
    }
}
