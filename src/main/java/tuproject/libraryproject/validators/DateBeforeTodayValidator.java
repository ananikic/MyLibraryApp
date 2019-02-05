package tuproject.libraryproject.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateBeforeTodayValidator implements ConstraintValidator<DateBeforeToday, LocalDate> {
    @Override
    public void initialize(DateBeforeToday dateBeforeToday) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.isBefore(LocalDate.now());
    }
}
