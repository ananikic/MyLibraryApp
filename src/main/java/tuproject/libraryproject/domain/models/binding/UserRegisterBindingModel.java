package tuproject.libraryproject.domain.models.binding;

import groovy.transform.EqualsAndHashCode;
import tuproject.libraryproject.validators.Email;
import tuproject.libraryproject.validators.Password;

public class UserRegisterBindingModel {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Password(containsDigit = true, containsLowerCase = true,
            containsUpperCase = true,
            message = "Password must contain digits, lower and upper case letters")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
