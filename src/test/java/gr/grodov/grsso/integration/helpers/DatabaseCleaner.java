package gr.grodov.grsso.integration.helpers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void cleanAll() {
        jdbcTemplate.execute("TRUNCATE TABLE oauth2_authorization, oauth2_authorization_consent, oauth2_registered_client, users, oauth2_session RESTART IDENTITY CASCADE");
    }
}
