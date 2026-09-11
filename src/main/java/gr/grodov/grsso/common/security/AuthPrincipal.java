package gr.grodov.grsso.common.security;

import gr.grodov.grsso.user.domain.entity.AuthProvider;

import java.util.UUID;

public interface AuthPrincipal {
    UUID getId();
    AuthProvider getProvider();
    String getEmail();
}
