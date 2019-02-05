package tuproject.libraryproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import tuproject.libraryproject.domain.models.view.UserViewModel;
import tuproject.libraryproject.services.BookService;
import tuproject.libraryproject.services.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController extends BaseController{
    private UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView index(){
        return this.view("index");
    }

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView home(Principal principal, ModelAndView modelAndView) {
        modelAndView.addObject("username", principal.getName());

        UserViewModel userViewModel = this.userService.extractUserByName(principal.getName());
        List<String> notReturnedBooks = this.userService.isReturnDateExpired(userViewModel.getId());

        if (!notReturnedBooks.isEmpty()){
            modelAndView.addObject("dateExpired", true);
            modelAndView.addObject("titles", notReturnedBooks);
        }
        return this.view("home", modelAndView);
    }
}
