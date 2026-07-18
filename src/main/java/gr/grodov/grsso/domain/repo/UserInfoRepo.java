package gr.grodov.grsso.domain.repo;

import gr.grodov.grsso.domain.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepo extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
    Boolean existsByEmail(String email);
}
