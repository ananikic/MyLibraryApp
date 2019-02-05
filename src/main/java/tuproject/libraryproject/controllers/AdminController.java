package tuproject.libraryproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import tuproject.libraryproject.domain.models.binding.UserEditBindingModel;
import tuproject.libraryproject.domain.models.view.UserViewModel;
import tuproject.libraryproject.services.RoleService;
import tuproject.libraryproject.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController extends BaseController{

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView showUsers(Principal principal, ModelAndView modelAndView){
        List<UserViewModel> userViewModels = this.userService.extractAllUsers()
                .stream().filter(userViewModel -> !userViewModel.getUsername().equals(principal.getName()))
                .collect(Collectors.toList());
        modelAndView.addObject("users", userViewModels);
        return this.view("users/show-users", modelAndView);
    }

    @GetMapping("/users/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView editUser(@PathVariable("id") String id, ModelAndView modelAndView){
        UserEditBindingModel userEditBindingModel = this.userService.extractUserForEditById(id);

        if (userEditBindingModel.getRoleAuthorities().contains("ROOT")){
            return this.redirect("/users");
        }


        modelAndView.addObject("userEditBindingModel", userEditBindingModel);
        modelAndView.addObject("roles", this.roleService.extractAllRoles());

        return this.view("users/edit-user", modelAndView);
    }

    @PostMapping("/users/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView editUserConfirm(@PathVariable("id") String id,
                                        @Valid @ModelAttribute("userEditBindingModel ")UserEditBindingModel userEditBindingModel,
                                        BindingResult bindingResult,
                                        ModelAndView modelAndView){
        if (bindingResult.hasErrors()){
            modelAndView.addObject("roles", this.roleService.extractAllRoles());
            return this.view("users/edit-user", modelAndView);
        }

        this.userService.insertEditedUser(userEditBindingModel);

        return this.redirect("/users");
    }




}
