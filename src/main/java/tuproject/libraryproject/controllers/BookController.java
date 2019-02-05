package tuproject.libraryproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import tuproject.libraryproject.domain.models.binding.BookBindingModel;
import tuproject.libraryproject.domain.models.view.BookViewModel;
import tuproject.libraryproject.domain.models.view.UserViewModel;
import tuproject.libraryproject.domain.models.view.UsersBookViewModel;
import tuproject.libraryproject.services.BookService;
import tuproject.libraryproject.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
public class BookController extends BaseController{
    private BookService bookService;
    private UserService userService;

    @Autowired
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("books/add")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public ModelAndView addBook(@ModelAttribute BookBindingModel bookBindingModel, ModelAndView modelAndView){
        modelAndView.addObject("bookBindingModel", bookBindingModel);
        return this.view("books/add-book", modelAndView);
    }
    @PostMapping("books/add")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public ModelAndView addBookConfirm(@Valid @ModelAttribute BookBindingModel bookBindingModel,
                                       BindingResult bindingResult, ModelAndView modelAndView) throws ParseException {
        if (bindingResult.hasErrors()){
            return this.view("books/add-book", modelAndView);
        }

        this.bookService.addBook(bookBindingModel);
        return this.redirect("/books/show");
    }

    @GetMapping("books/show")
    @PreAuthorize("hasAuthority('USER')")
    public ModelAndView showBooks(ModelAndView modelAndView, @RequestParam(defaultValue = "") String contains,
                                  @RequestParam(defaultValue = "") String title,
                                  @RequestParam(defaultValue = "",required = false) String authorName,
                @RequestParam(defaultValue = "", required = false) String dateBefore,
                @RequestParam(defaultValue = "", required = false) String dateAfter){
        List<BookViewModel> bookViewModels=new ArrayList<>();

        if (title.isEmpty()&&authorName.isEmpty()&&dateBefore.isEmpty()&&dateAfter.isEmpty()&&contains.isEmpty()) {
                  bookViewModels = this.bookService.extractAllBooks();
        }else if (!contains.isEmpty()){
            bookViewModels = this.bookService.extractBooksByTitleContains(contains);
        } else{
            List<BookViewModel> bookViewModelsByAuthorOrTitle = this.bookService.extractBooksByTitleOrAuthor(title, authorName);
            List<BookViewModel> booksByReleaseDateBefore = this.bookService.extractBooksByReleaseDateBefore(dateBefore);
            List<BookViewModel> booksByReleaseDateAfter = this.bookService.extractBooksByReleaseDateAfter(dateAfter);
            bookViewModels.addAll(bookViewModelsByAuthorOrTitle);
            bookViewModels.addAll(booksByReleaseDateBefore);
            bookViewModels.addAll(booksByReleaseDateAfter);
        }
        List<BookViewModel> withoutDuplicates = bookViewModels.stream()
                .sorted(Comparator.comparing(BookViewModel::getTitle))
                .filter(distinctByKey(BookViewModel::getTitle))
                .collect(Collectors.toList());
        modelAndView.addObject("books", withoutDuplicates);

            for (BookViewModel book : withoutDuplicates) {
                String username = this.bookService.extractUserWhichOwnsTheBook(book.getTitle());
                book.setUser(username);
            }

        return this.view("books/show-books", modelAndView);
    }


    @GetMapping("books/edit/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public ModelAndView editBooks(@PathVariable("id")String id, ModelAndView modelAndView){
        BookBindingModel bookBindingModel = this.bookService.extractBookByIdForEdit(id);
        modelAndView.addObject("bookBindingModel", bookBindingModel);
        return this.view("books/edit-book", modelAndView);
    }

    @PostMapping("books/edit/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public ModelAndView editBooksConfirm(@PathVariable("id")String id,
                                         @Valid @ModelAttribute("bookBindingModel") BookBindingModel bookBindingModel,
                                         BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return this.view("books/edit-book");
        }
        this.bookService.insertEditedBook(bookBindingModel);

        return this.redirect("/books/show");
    }

    @GetMapping("books/delete/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public ModelAndView deleteBook(@PathVariable("id")String id, ModelAndView modelAndView){
        BookBindingModel bookBindingModel = this.bookService.extractBookByIdForEdit(id);
        modelAndView.addObject("bookBindingModel", bookBindingModel);
        return this.view("books/delete-book", modelAndView);
    }

    @PostMapping("books/delete/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public ModelAndView deleteBookConfirm(@PathVariable("id")String id){

        this.bookService.deleteBookById(id);
        return this.redirect("/books/show");
    }

    @GetMapping("books/take/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ModelAndView takeBook(@PathVariable("id")String id,
                                 Principal principal){

        UserViewModel userViewModel = this.userService.extractUserByName(principal.getName());
        System.out.println();
        this.userService.takeBook(id, userViewModel);
        return this.redirect("/home");
    }

    @GetMapping("books/mine")
    @PreAuthorize("hasAuthority('USER')")
    public ModelAndView showMine(Principal principal, ModelAndView modelAndView){
        UserViewModel userViewModel = this.userService.extractUserByName(principal.getName());
        List<UsersBookViewModel> bookViewModels = this.userService.extractBooksOfUser(userViewModel);
        modelAndView.addObject("books", bookViewModels);
        return this.view("books/show-mine", modelAndView);
    }

    private static <T> Predicate<T>  distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
