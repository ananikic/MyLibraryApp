package tuproject.libraryproject.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "We believe you can read more than five books in a month," +
        " but please return some of your taken books and then come back and get some more! :-)")
public class BookLimitException extends RuntimeException {

    public BookLimitException() {
    }
}


