package gr.grodov.grsso.user.domain.repo;

import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.domain.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInfoRepo extends JpaRepository<UserInfo, UUID> {
    Optional<UserInfo> findByEmailAndProvider(String email, AuthProvider provider);
    Optional<UserInfo> findByEmail(String email);
    Boolean existsByEmailAndProvider(String email, AuthProvider provider);
    void deleteByEnabledFalseAndCreatedAtBefore(Instant instant);
}
