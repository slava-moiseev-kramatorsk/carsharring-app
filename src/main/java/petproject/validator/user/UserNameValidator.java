package petproject.validator.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UserNameValidator implements ConstraintValidator<UserName, String> {
    private static final String REGEX_FIRS_AND_LAST_NAME = "[A-Za-z]+";
    private static final int VALID_LENGTH_NAME = 20;

    @Override
    public boolean isValid(
            String inputName,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        return Pattern.compile(REGEX_FIRS_AND_LAST_NAME)
                .matcher(inputName).matches()
                && inputName.length() < VALID_LENGTH_NAME;
    }
}
