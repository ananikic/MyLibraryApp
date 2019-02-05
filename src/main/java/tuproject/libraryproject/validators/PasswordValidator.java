package tuproject.libraryproject.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private  boolean containsDigits;
    private boolean containsLowerCase;
    private boolean containsUpperCase;
    private boolean containsSpecialSymbols;

    @Override
    public void initialize(Password password) {
        this.containsDigits = password.containsDigit();
        this.containsLowerCase = password.containsLowerCase();
        this.containsUpperCase = password.containsUpperCase();
        this.containsSpecialSymbols = password.containsSpecialSymbols();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        Pattern lowerCase = Pattern.compile("[a-z]");
        Matcher matcherLowerCase = lowerCase.matcher(password);
        if (!matcherLowerCase.find() && this.containsLowerCase){
            return false;
        }

        Pattern upperCase = Pattern.compile("[A-Z]");
        Matcher matcherUpperCase = upperCase.matcher(password);
        if (!matcherUpperCase.find() && this.containsUpperCase){
            return false;
        }

        Pattern digits = Pattern.compile("[0-9]");
        Matcher matcherDigits = digits.matcher(password);
        if (!matcherDigits.find() && this.containsDigits){
            return  false;
        }

        Pattern symbol = Pattern.compile("[!@#$%^&*()_+<>?]");
        Matcher matcherSymbol = symbol.matcher(password);
        if (!matcherSymbol.find() && this.containsSpecialSymbols) {
            return false;
        }

        return true;
    }
}
