package tuproject.libraryproject.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {
    @Override
    public void initialize(Email email) {

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        Pattern emailPattern = Pattern.compile("^[a-zA-Z][\\w.-]+(@)[a-zA-Z]+([.])[a-zA-Z]{2,6}$");
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()){
            return false;
        }
        return true;
    }
}
