package tuproject.libraryproject.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "This book already exists")
public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException() {
    }

}
