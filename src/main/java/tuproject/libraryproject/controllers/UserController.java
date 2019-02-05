package tuproject.libraryproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import tuproject.libraryproject.domain.models.binding.UserLoginBindingModel;
import tuproject.libraryproject.domain.models.binding.UserRegisterBindingModel;
import tuproject.libraryproject.domain.models.view.UserViewModel;
import tuproject.libraryproject.domain.models.view.UsersBookViewModel;
import tuproject.libraryproject.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController extends BaseController{
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView register(@ModelAttribute("user") UserRegisterBindingModel userRegisterBindingModel,
                                 ModelAndView modelAndView){
        modelAndView.addObject("user", userRegisterBindingModel);
        return this.view("users/register-user",modelAndView);
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerConfirm(@Valid @ModelAttribute("user")
                                                    UserRegisterBindingModel userRegisterBindingModel,
                                        BindingResult bindingResult,
                                        ModelAndView modelAndView){
        if (bindingResult.hasErrors()){
            return this.view("users/register-user", modelAndView);
        }

        if (!this.userService.registerUser(userRegisterBindingModel)){
            return this.view("users/register-user");
        }
        return this.redirect("/home");
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login(){
        return this.view("users/login-user");
    }

    @PostMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView loginConfirm(@ModelAttribute("userBindingModel")UserLoginBindingModel userLoginBindingModel){
        if (!this.userService.loginUser(userLoginBindingModel)){
            return this.view("users/login-user");
        }
        return this.view("home");
    }



}
