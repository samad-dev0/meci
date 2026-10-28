package meci_e_services.repository;

import meci_e_services.entity.AppUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppUsersRepository extends JpaRepository<AppUsers, Long> {

    Optional<AppUsers> findByEmail(String email);
    Optional<AppUsers> findByEmailIgnoreCase(String email);
    Optional<AppUsers> findFirstBySuperAdminTrue();
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}