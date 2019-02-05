package tuproject.libraryproject.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tuproject.libraryproject.domain.entities.Book;
import tuproject.libraryproject.domain.entities.User;
import tuproject.libraryproject.domain.entities.UserRole;
import tuproject.libraryproject.domain.models.binding.UserEditBindingModel;
import tuproject.libraryproject.domain.models.binding.UserLoginBindingModel;
import tuproject.libraryproject.domain.models.binding.UserRegisterBindingModel;
import tuproject.libraryproject.domain.models.view.UserViewModel;
import tuproject.libraryproject.domain.models.view.UsersBookViewModel;
import tuproject.libraryproject.errors.BookIsTakenException;
import tuproject.libraryproject.errors.BookLimitException;
import tuproject.libraryproject.repositories.BookRepository;
import tuproject.libraryproject.repositories.RoleRepository;
import tuproject.libraryproject.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RoleRepository roleRepository;
    private BookRepository bookRepository;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
        this.bookRepository = bookRepository;
    }



    @Override
    public boolean registerUser(UserRegisterBindingModel userRegisterBindingModel) {
        if (!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
            return false;
        }

        User user = this.modelMapper.map(userRegisterBindingModel, User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));

        this.insertUserRoles();

        if (this.userRepository.count() == 0) {
            user.getAuthorities().add(this.roleRepository.findByAuthority("ROOT"));
            user.getAuthorities().add(this.roleRepository.findByAuthority("ADMIN"));
            user.getAuthorities().add(this.roleRepository.findByAuthority("MODERATOR"));
            user.getAuthorities().add(this.roleRepository.findByAuthority("USER"));
        } else {
            user.getAuthorities().add(this.roleRepository.findByAuthority("USER"));
        }
        this.userRepository.save(user);
        return true;
    }

    @Override
    public boolean loginUser(UserLoginBindingModel userLoginBindingModel) {
        UserDetails userDetails = this.loadUserByUsername(userLoginBindingModel.getUsername());

        if (!userLoginBindingModel.getUsername().equals(userDetails.getUsername()) || !userLoginBindingModel.getPassword().equals(userDetails.getPassword())) {
            return false;
        }

        return true;
    }

    @Override
    public UserViewModel extractUserByName(String name) {
        User userFromDb = this.userRepository.findByUsername(name).orElse(null);
        if (userFromDb==null){
            throw new UsernameNotFoundException("Username not found!");
        }
        return this.modelMapper.map(userFromDb, UserViewModel.class);
    }

    @Override
    public List<UserViewModel> extractAllUsers() {
        List<User> usersFromDataBase = this.userRepository.findAll();
        List<UserViewModel> userViewModels = new ArrayList<>();
        for (User user : usersFromDataBase) {
            UserViewModel userViewModel = this.modelMapper.map(user, UserViewModel.class);
            userViewModel.setRoles(user.getAuthorities().stream().map(UserRole::getAuthority).collect(Collectors.joining(", ")));
            userViewModels.add(userViewModel);
        }

        return userViewModels;
    }

    @Override
    public UserEditBindingModel extractUserForEditById(String id) {
        User userFromDatabase = this.userRepository.findById(id).orElse(null);

        if (userFromDatabase == null) {
            throw new IllegalArgumentException("Non-existent user.");
        }

        UserEditBindingModel userBindingModel = this.modelMapper.map(userFromDatabase, UserEditBindingModel.class);

        for (UserRole userRole : userFromDatabase.getAuthorities()) {
            userBindingModel.getRoleAuthorities().add(userRole.getAuthority());
        }

        return userBindingModel;
    }

    @Override
    public void insertEditedUser(UserEditBindingModel userEditBindingModel) {
        User user = this.userRepository.findByUsername(userEditBindingModel.getUsername()).orElse(null);
        if (user == null){
            throw new UsernameNotFoundException("Username not found!");
        }
        user.getAuthorities().clear();

        if (userEditBindingModel.getRoleAuthorities().contains("ADMIN")) {
            user.getAuthorities().add(this.roleRepository.findByAuthority("USER"));
            user.getAuthorities().add(this.roleRepository.findByAuthority("MODERATOR"));
            user.getAuthorities().add(this.roleRepository.findByAuthority("ADMIN"));
        } else if (userEditBindingModel.getRoleAuthorities().contains("MODERATOR")) {
            user.getAuthorities().add(this.roleRepository.findByAuthority("USER"));
            user.getAuthorities().add(this.roleRepository.findByAuthority("MODERATOR"));
        } else {
            user.getAuthorities().add(this.roleRepository.findByAuthority("USER"));
        }

        this.userRepository.save(user);

    }

    @Override
    public List<UsersBookViewModel> extractBooksOfUser(UserViewModel userViewModel) {
        String id = userViewModel.getId();
        User user = this.userRepository.findById(id).orElse(null);
        List<Book> usersBooks = user.getUsersBooks();

        List<UsersBookViewModel> bookViewModels = new ArrayList<>();
        for (Book usersBook : usersBooks) {
            UsersBookViewModel model = modelMapper.map(usersBook, UsersBookViewModel.class);
            bookViewModels.add(model);
        }
        return bookViewModels;
    }

    @Override
    public void takeBook(String id, UserViewModel userViewModel) {
        User user = modelMapper.map(userViewModel, User.class);
        Book bookToTake = this.bookRepository.findOne(id);

        if (this.extractBooksOfUser(userViewModel).size()>=5){
            throw new BookLimitException();
        }

        if (!bookToTake.isAvailable()){
            throw new BookIsTakenException();
        }

        bookToTake.setAvailable(false);
        bookToTake.setDateTaken(LocalDate.now());
        bookToTake.setReturnDate(LocalDate.now().plusDays(30));
        bookToTake.setUser(user);
        user.getUsersBooks().add(bookToTake);

        this.bookRepository.save(bookToTake);


    }

    @Override
    public List<String> isReturnDateExpired(String id) {
        User user = this.userRepository.findOne(id);
        List<Book> usersBooks = user.getUsersBooks();
        List<String> notReturnedBooksTitles = new ArrayList<>();
        for (Book usersBook : usersBooks) {
            if (usersBook.getReturnDate().isBefore(LocalDate.now())){
                String title = usersBook.getTitle();
                notReturnedBooksTitles.add(title);
            }
        }
        return notReturnedBooksTitles;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("Username not found!");
        }
        return user;
    }


    private void insertUserRoles() {
        if (this.roleRepository.count() == 0) {
            UserRole root = new UserRole();
            root.setAuthority("ROOT");
            UserRole admin = new UserRole();
            admin.setAuthority("ADMIN");
            UserRole moderator = new UserRole();
            moderator.setAuthority("MODERATOR");
            UserRole user = new UserRole();
            user.setAuthority("USER");

            this.roleRepository.save(root);
            this.roleRepository.save(admin);
            this.roleRepository.save(moderator);
            this.roleRepository.save(user);
        }
    }




}
