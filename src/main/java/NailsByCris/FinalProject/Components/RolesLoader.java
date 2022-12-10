package NailsByCris.FinalProject.Components;

import NailsByCris.FinalProject.Model.Role;
import NailsByCris.FinalProject.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RolesLoader implements CommandLineRunner{

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        String[] roles = {"Admin", "User"};
        for (String roleString: roles) {
            Role role = roleRepository.findByRole(roleString);
            if (role == null) {
                role = new Role(roleString);
                roleRepository.save(role);
            }
        }

    }
}
