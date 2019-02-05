package tuproject.libraryproject.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Someone already owns the book. ")
public class BookIsTakenException extends RuntimeException {
    public BookIsTakenException() {
    }
}
