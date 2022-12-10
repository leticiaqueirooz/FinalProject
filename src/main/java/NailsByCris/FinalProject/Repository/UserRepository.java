package NailsByCris.FinalProject.Repository;

import NailsByCris.FinalProject.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String Login);
}
