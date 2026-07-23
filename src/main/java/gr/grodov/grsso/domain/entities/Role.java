package gr.grodov.grsso.domain.entities;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public @NonNull String getAuthority() {
        return name();
    }
}
