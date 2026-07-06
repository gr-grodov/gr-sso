package gr.grodov.grsso.domain.repo;

import gr.grodov.grsso.domain.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepo extends CrudRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
    Boolean existsByEmail(String email);
}
