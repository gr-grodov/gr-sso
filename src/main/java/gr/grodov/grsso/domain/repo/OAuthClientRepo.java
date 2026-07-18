package gr.grodov.grsso.domain.repo;

import gr.grodov.grsso.domain.entities.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface OAuthClientRepo extends JpaRepository<OAuthClient, String> {
}
