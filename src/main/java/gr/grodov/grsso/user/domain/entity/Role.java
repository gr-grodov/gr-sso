package gr.grodov.grsso.user.domain.entity;

import org.jspecify.annotations.NonNull;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.GrantedAuthority;

@NamedInterface("domain")
public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public @NonNull String getAuthority() {
        return name();
    }
}
