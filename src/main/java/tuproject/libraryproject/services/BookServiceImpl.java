package tuproject.libraryproject.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuproject.libraryproject.domain.entities.Book;
import tuproject.libraryproject.domain.entities.User;
import tuproject.libraryproject.domain.models.binding.BookBindingModel;
import tuproject.libraryproject.domain.models.view.BookViewModel;
import tuproject.libraryproject.domain.models.view.UserViewModel;
import tuproject.libraryproject.errors.BookAlreadyExistsException;
import tuproject.libraryproject.errors.BookCantBeDeletedException;
import tuproject.libraryproject.errors.BookNotFoundException;
import tuproject.libraryproject.repositories.BookRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BookServiceImpl implements BookService{

    private BookRepository bookRepository;
    private ModelMapper modelMapper;


    @Autowired
    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;

    }

    @Override
    public void addBook(BookBindingModel bookBindingModel)  {
        List<Book> all = this.bookRepository.findAll();
        for (Book book : all) {
            if (book.getTitle().equals(bookBindingModel.getTitle())) {
                throw new BookAlreadyExistsException();
            }
        }
        Book book = this.modelMapper.map(bookBindingModel, Book.class);
        this.bookRepository.save(book);
    }

    @Override
    public void deleteBookById(String id) {
        Book book = this.bookRepository.findOne(id);
        if (!book.isAvailable()){
            throw new BookCantBeDeletedException();
        }
        this.bookRepository.delete(book);
    }

    @Override
    public List<BookViewModel> extractAllBooks() {
        List<Book> booksFromDb = this.bookRepository.findAll();
        return this.convertToBookViewModels(booksFromDb);
    }


    @Override
    public List<BookViewModel> extractBooksByTitleOrAuthor(String title, String author) {
        List<BookViewModel> bookViewModels = new ArrayList<>();
        List<Book> allByTitleOrAuthorName= new ArrayList<>();

        if (!title.isEmpty()&&!author.isEmpty()){
             allByTitleOrAuthorName = this.bookRepository.findAllByTitleContainsAndAuthorName(title, author);
        }else if (!title.isEmpty()){
           allByTitleOrAuthorName = this.bookRepository.findAllByTitleContains(title);
        }else if (!author.isEmpty()){
            allByTitleOrAuthorName = this.bookRepository.findAllByAuthorNameContains(author);
        }
        for (Book book : allByTitleOrAuthorName) {
            BookViewModel bookViewModel = this.modelMapper.map(book, BookViewModel.class);
            if (book.isAvailable()){
                bookViewModel.setIsAvailable("YES");
            }else {
                bookViewModel.setIsAvailable("NO");
            }
            bookViewModels.add(bookViewModel);
        }
        List<BookViewModel> bookViewModelsWithoutDuplicates = bookViewModels
                .stream().distinct().collect(Collectors.toList());
        return bookViewModelsWithoutDuplicates;
    }


    @Override
    public List<BookViewModel> extractBooksByTitleContains(String partOfTitle) {
        if (partOfTitle.isEmpty()){
            return new ArrayList<>();
        }
        List<Book> allByTitleContains = this.bookRepository.findAllByTitleContains(partOfTitle);

        return this.convertToBookViewModels(allByTitleContains);
    }




    @Override
    public BookBindingModel extractBookByIdForEdit(String id) {
        Book book = this.bookRepository.findOne(id);
        BookBindingModel bookBindingModel = this.modelMapper.map(book, BookBindingModel.class);
        return bookBindingModel;
    }

    @Override
    public void insertEditedBook(BookBindingModel bookBindingModel) {
        Book book = this.modelMapper.map(bookBindingModel, Book.class);
        this.bookRepository.save(book);
    }


    @Override
    public String extractUserWhichOwnsTheBook(String title) {
        Book book = this.bookRepository.findByTitle(title).orElse(null);
        if (book == null){
            throw new BookNotFoundException();
        }

        if (!book.isAvailable()) {
          User user = book.getUser();
          return  this.modelMapper.map(user, UserViewModel.class).getUsername();
        }else{
            return "";
        }
    }

    @Override
    public List<BookViewModel> extractBooksByReleaseDateBefore(String dateBeforeAsString) {
        if (dateBeforeAsString.isEmpty()){
            return new ArrayList<>();
        }
        LocalDate dateBefore = LocalDate.parse(dateBeforeAsString);
        List<Book> allByReleaseDateBefore = this.bookRepository.findAllByReleaseDateBefore(dateBefore);
        return this.convertToBookViewModels(allByReleaseDateBefore);
    }

    @Override
    public List<BookViewModel> extractBooksByReleaseDateAfter(String dateAfterAsString) {
        if (dateAfterAsString.isEmpty()){
            return new ArrayList<>();
        }
        LocalDate dateAfter = LocalDate.parse(dateAfterAsString);
        List<Book> allByReleaseDateAfter = this.bookRepository.findAllByReleaseDateAfter(dateAfter);
        return this.convertToBookViewModels(allByReleaseDateAfter);
    }





    private List<BookViewModel> convertToBookViewModels(List<Book> books){
        List<BookViewModel> bookViewModels = new ArrayList<>();
        for (Book book : books) {
            BookViewModel bookViewModel = this.modelMapper.map(book, BookViewModel.class);
            if (book.isAvailable()){
                bookViewModel.setIsAvailable("YES");
            }else {
                bookViewModel.setIsAvailable("NO");
            }
            bookViewModels.add(bookViewModel);

        }
        return bookViewModels;
    }


}
