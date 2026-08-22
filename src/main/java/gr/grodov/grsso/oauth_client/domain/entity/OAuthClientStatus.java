package gr.grodov.grsso.oauth_client.domain.entity;

import org.springframework.modulith.NamedInterface;

@NamedInterface("domain")
public enum OAuthClientStatus {
    ACTIVE,
    DISABLED,
}
