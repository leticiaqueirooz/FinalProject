package NailsByCris.FinalProject.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import NailsByCris.FinalProject.Model.Role;
import NailsByCris.FinalProject.Model.User;
import NailsByCris.FinalProject.Repository.UserRepository;
import NailsByCris.FinalProject.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
    @RequestMapping("/user")
    public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder cryptography;



    @GetMapping("/new")
        public String addUser(Model model) {
            model.addAttribute("user", new User());
            return "/public-create-user";
        }


    @GetMapping("/index")
            public String index(@CurrentSecurityContext(expression="authentication.name")
                    String login) {

                User user = userRepository.findByLogin(login);

                String redirectURL = "";
                if (hasAuthorization(user, "Admin")) {
                    redirectURL = "/auth/admin/admin-index";
                } else if (hasAuthorization(user, "User")) {
                    redirectURL = "/auth/user/user-index";
                }
                return redirectURL;
            }
    /**
     * Method to verify what roles the user have within the application
     * */
    private boolean hasAuthorization(User user, String role) {
        for (Role pp : user.getRoles()) {
            if (pp.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }



    @PostMapping("/save")
        public String saveUser(@Valid User user, BindingResult result,
                               Model model, RedirectAttributes attributes) {
            if (result.hasErrors()) {
                return "/public-create-user";
            }
            User usr = userRepository.findByLogin(user.getLogin());
            if (usr != null) {
                model.addAttribute("loginUnavailable", "Login unavailable");
                return "/public-create-user";
            }
            // Look for the user role in the database and set it to the new user
            // Every new user will be associated with at least the user role.
            Role role = roleRepository.findByRole("User");
            List<Role> roles = new ArrayList<Role>();
            roles.add(role);

            user.setRoles(roles); // Associate this new user to a User role

            // Using the cryptography object to encode users password!
            String cryptographyPassword = cryptography.encode(user.getPassword());
            user.setPassword(cryptographyPassword);

            userRepository.save(user);
            attributes.addFlashAttribute("message", "User created!");
            return "redirect:/user/new";
        }
    @RequestMapping("/admin/list")
        public String listUsers(Model model) {
            model.addAttribute("ListOfUsers", userRepository.findAll());
            return "/auth/admin/admin-users";
        }

    @GetMapping("/admin/delete/{id}")
        public String deleteUser(@PathVariable("id") long id, Model model) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Id:" + id + " is not valid"));
            userRepository.delete(user);
            return "redirect:/user/admin/list";
    }
    @GetMapping("/edit/{id}")
        public String updateUser(@PathVariable("id") long id, Model model) {
            Optional<User> oldUser = userRepository.findById(id);
            if (!oldUser.isPresent()) {
                throw new IllegalArgumentException("User: " + id + " not valid!");
            }
            User user = oldUser.get();
            model.addAttribute("user", user);
            return "/auth/user/user-edit-user";
        }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") long id,
                             @Valid User user, BindingResult result,
                             RedirectAttributes attributes) {
        if (result.hasErrors()) {
            user.setId(id);
            return "/auth/user/user-edit-user";
        }
        userRepository.save(user);
        attributes.addFlashAttribute("message", "User Updated!");
        return "redirect:/user/admin/list";
    }
    @GetMapping("/editRole/{id}")
    public String selectRole(@PathVariable("id") long id, Model model) {
        Optional<User> oldUser = userRepository.findById(id);
        if (!oldUser.isPresent()) {
            throw new IllegalArgumentException("User id :" + id + " is not valid");
        }
        User user = oldUser.get();
        model.addAttribute("User", user);
        model.addAttribute("listRoles", roleRepository.findAll());

        return "/auth/admin/admin-editRole";
    }
    @PostMapping("/editRole/{id}")
    public String selectRole(@PathVariable("id") long idUser,
                                @RequestParam(value = "pps", required=false) int[] pps,
                                User user,
                                RedirectAttributes attributes) {
        if (pps == null) {
            user.setId(idUser);
            attributes.addFlashAttribute("message", "At lest one role has to be associated with this user");
            return "redirect:/user/editRole/"+idUser;
        } else {
            //Look for the roles inside the database
            List<Role> roles = new ArrayList<Role>();
            for (int i = 0; i < pps.length; i++) {
                long idRole = pps[i];
                Optional<Role> roleOptional = roleRepository.findById(idRole);
                if (roleOptional.isPresent()) {
                    Role role = roleOptional.get();
                    roles.add(role);
                }
            }
            Optional<User> userOptional = userRepository.findById(idUser);
            if (userOptional.isPresent()) {
                User usr = userOptional.get();
                usr.setRoles(roles); // Associate roles to the user
                userRepository.save(usr);
            }
        }
        return "redirect:/user/admin/list";
    }
    }

