package gr.grodov.grsso.domain.repo;

import gr.grodov.grsso.domain.entities.user.AuthProvider;
import gr.grodov.grsso.domain.entities.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepo extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmailAndProvider(String email, AuthProvider provider);
    Optional<UserInfo> findByEmail(String email);
    Boolean existsByEmail(String email);
}
