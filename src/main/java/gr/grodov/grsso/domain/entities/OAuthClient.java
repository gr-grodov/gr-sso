package gr.grodov.grsso.domain.entities;

import gr.grodov.grsso.domain.config.StringSetConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth2_registered_client")
public class OAuthClient {

    @Id
    @Column(length = 100, nullable = false)
    private String id;

    @Column(name = "client_id", length = 100, nullable = false)
    private String clientId;

    @Column(name = "client_id_issued_at", nullable = false)
    private Instant clientIdIssuedAt;

    @Column(name = "client_secret", length = 200)
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @Column(name = "client_name", length = 200, nullable = false)
    private String clientName;

    @Convert(converter = StringSetConverter.class)
    @Column(name = "client_authentication_methods")
    private Set<String> clientAuthenticationMethods;

    @Convert(converter = StringSetConverter.class)
    @Column(name = "authorization_grant_types")
    private Set<String> authorizationGrantTypes;

    @Convert(converter = StringSetConverter.class)
    @Column(name = "redirect_uris")
    private Set<String> redirectUris;

    @Convert(converter = StringSetConverter.class)
    @Column(name = "scopes")
    private Set<String> scopes;

    @Column(name = "client_settings", length = 2000, nullable = false)
    private String clientSettings;

    @Column(name = "token_settings", length = 2000, nullable = false)
    private String tokenSettings;

}