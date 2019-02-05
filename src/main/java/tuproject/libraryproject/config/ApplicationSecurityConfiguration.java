package tuproject.libraryproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import tuproject.libraryproject.services.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserService userService;

    @Autowired
    public ApplicationSecurityConfiguration(UserService userService) {
        this.userService = userService;
    }


    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();

        csrfTokenRepository.setSessionAttributeName("_csrf");
        return csrfTokenRepository;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

            http
                    .cors().disable()
                    .csrf()
                    .csrfTokenRepository(this.csrfTokenRepository())
                .and()
                .authorizeRequests()
                    .antMatchers("/login", "/register", "/").permitAll()
                    .antMatchers("/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/home")
                .and()
                .rememberMe()
                    .rememberMeParameter("rememberMe")
                    .key("REMEMBERMEKEY")
                    .userDetailsService(this.userService)
                    .rememberMeCookieName("REMEMBERMECOOKIE")
                    .tokenValiditySeconds(10)
                .and()
                .exceptionHandling()
                     .accessDeniedPage("/unauthorized");


    }
}
