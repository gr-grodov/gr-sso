package gr.grodov.grsso.domain.entities.oauth;

import gr.grodov.grsso.domain.converter.AuthorizationGrantTypeSetConverter;
import gr.grodov.grsso.domain.converter.ClientAuthenticationMethodSetConverter;
import gr.grodov.grsso.domain.converter.StringSetConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth2_registered_client")
@EntityListeners(AuditingEntityListener.class)
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

    @Convert(converter = ClientAuthenticationMethodSetConverter.class)
    @Column(name = "client_authentication_methods")
    private Set<OAuthClientAuthenticationMethod> clientAuthenticationMethods;

    @Convert(converter = AuthorizationGrantTypeSetConverter.class)
    @Column(name = "authorization_grant_types")
    private Set<OAuthAuthorizationGrantType> authorizationGrantTypes;

    @Convert(converter = StringSetConverter.class)
    @Column(name = "redirect_uris")
    private Set<String> redirectUris;

    @Convert(converter = StringSetConverter.class)
    @Column(name = "post_logout_redirect_uris")
    private Set<String> postLogoutRedirectUris;

    @Convert(converter = StringSetConverter.class)
    @Column(name = "scopes")
    private Set<String> scopes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "client_settings", length = 2000, nullable = false)
    private OAuthClientSettings clientSettings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "token_settings", length = 2000, nullable = false)
    private OAuthTokenSettings tokenSettings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthClientStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "update_at")
    private Instant updatedAt;

    @Version
    private Long version;
}