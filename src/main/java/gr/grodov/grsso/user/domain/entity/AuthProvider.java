package gr.grodov.grsso.user.domain.entity;

import org.springframework.modulith.NamedInterface;

@NamedInterface("domain")
public enum AuthProvider {
    LOCAL,
    GOOGLE
}
