package gr.grodov.grsso.repo;

import gr.grodov.grsso.models.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepo extends CrudRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
}
