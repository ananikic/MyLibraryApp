package tuproject.libraryproject.services;

import tuproject.libraryproject.domain.models.binding.BookBindingModel;
import tuproject.libraryproject.domain.models.view.BookViewModel;

import java.text.ParseException;
import java.util.List;

public interface BookService {
    void addBook(BookBindingModel bookBindingModel) throws ParseException;
    void deleteBookById(String id);
    List<BookViewModel> extractAllBooks();
    BookBindingModel extractBookByIdForEdit(String id);
    void insertEditedBook(BookBindingModel bookBindingModel);
    List<BookViewModel> extractBooksByReleaseDateBefore(String dateBeforeAsString);
    List<BookViewModel> extractBooksByReleaseDateAfter(String dateAfterAsString);
    List<BookViewModel> extractBooksByTitleOrAuthor(String title, String author);
    List<BookViewModel> extractBooksByTitleContains(String partOfTitle);

    String extractUserWhichOwnsTheBook(String title);


}


