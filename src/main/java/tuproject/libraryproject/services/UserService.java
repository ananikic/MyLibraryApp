package tuproject.libraryproject.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import tuproject.libraryproject.domain.models.binding.UserEditBindingModel;
import tuproject.libraryproject.domain.models.binding.UserLoginBindingModel;
import tuproject.libraryproject.domain.models.binding.UserRegisterBindingModel;
import tuproject.libraryproject.domain.models.view.UserViewModel;
import tuproject.libraryproject.domain.models.view.UsersBookViewModel;

import java.util.List;

public interface UserService extends UserDetailsService {
    boolean registerUser(UserRegisterBindingModel userRegisterBindingModel);

    boolean loginUser(UserLoginBindingModel userLoginBindingModel);

    UserViewModel extractUserByName(String name);

    List<UserViewModel> extractAllUsers();

    UserEditBindingModel extractUserForEditById(String id);

    void insertEditedUser(UserEditBindingModel userEditBindingModel);

    List<UsersBookViewModel> extractBooksOfUser(UserViewModel userViewModel);

    void takeBook(String id, UserViewModel userViewModel);

    List<String> isReturnDateExpired(String id);




}
