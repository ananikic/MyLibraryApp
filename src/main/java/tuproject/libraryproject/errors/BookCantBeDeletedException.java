package tuproject.libraryproject.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Someone owns the book. It can't be deleted at the time.")
public class BookCantBeDeletedException extends RuntimeException {
    public BookCantBeDeletedException() {
    }
}
