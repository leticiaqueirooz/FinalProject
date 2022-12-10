package NailsByCris.FinalProject.Repository;

import NailsByCris.FinalProject.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(String role);
}
