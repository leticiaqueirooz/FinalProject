package NailsByCris.FinalProject.Security;

import NailsByCris.FinalProject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // Create an userRepository
    @Autowired
    private UserRepository userRepository;

    // Create a loginSuccessful object so we can use it
    @Autowired
    LoginSuccessful loginSuccessful;

    // Method to generate an object of type user details, that contains login, password and roles
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception{
        OurUsersDetails ourUsersDetails = new OurUsersDetails(userRepository);
        return ourUsersDetails;
    }

    // Creating object to generate the cryptography of passwords!
    // Bean annotation means that as soon as this class is running, this object will be created and
    // made available to other classes
    @Bean
    public BCryptPasswordEncoder generateCryptography() {
        BCryptPasswordEncoder cryptography = new BCryptPasswordEncoder();
        return cryptography;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/auth/user/*").hasAnyAuthority("User","Admin")
                .antMatchers("/auth/admin/*").hasAnyAuthority("Admin")
                .antMatchers("/user/admin/*").hasAnyAuthority("Admin")
                .and()
                .exceptionHandling().accessDeniedPage("/auth/auth-access-denied")
                .and()
                .formLogin().successHandler(loginSuccessful)
                .loginPage("/login").permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").permitAll();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Object that have the user details
        UserDetailsService userDetails = userDetailsServiceBean();
        // Object to encode user's passwords!
        BCryptPasswordEncoder cryptography = generateCryptography();

        auth.userDetailsService(userDetails).passwordEncoder(cryptography);
    }

}
