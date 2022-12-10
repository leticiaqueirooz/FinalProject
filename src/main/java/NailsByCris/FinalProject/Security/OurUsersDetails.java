package NailsByCris.FinalProject.Security;

import NailsByCris.FinalProject.Model.Role;
import NailsByCris.FinalProject.Model.User;
import NailsByCris.FinalProject.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
@Service
@Transactional
public class OurUsersDetails implements UserDetailsService {

    private UserRepository userRepository;

    public OurUsersDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByLogin(username);
        if (user != null) {
            Set<GrantedAuthority> userRoles = new HashSet<GrantedAuthority>();
            for (Role role : user.getRoles()) {
                GrantedAuthority pp = new SimpleGrantedAuthority(role.getRole());
                userRoles.add(pp);
            }
            org.springframework.security.core.userdetails.User usr = new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), userRoles);
            return usr;
        } else {
            throw new UsernameNotFoundException("User not found!");
        }
    }
}
