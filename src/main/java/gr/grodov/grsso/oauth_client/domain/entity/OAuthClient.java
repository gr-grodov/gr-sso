package gr.grodov.grsso.oauth_client.domain.entity;

import gr.grodov.grsso.oauth_client.domain.converter.OAuthScopeSetConverter;
import gr.grodov.grsso.common.domain.BaseEntity;
import gr.grodov.grsso.oauth_client.domain.converter.AuthorizationGrantTypeSetConverter;
import gr.grodov.grsso.oauth_client.domain.converter.ClientAuthenticationMethodSetConverter;
import gr.grodov.grsso.common.domain.converter.StringSetConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.modulith.NamedInterface;

import java.time.Instant;
import java.util.Set;


@NamedInterface("domain")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth2_registered_client")
public class OAuthClient extends BaseEntity {

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

    @Convert(converter = OAuthScopeSetConverter.class)
    @Column(name = "scopes")
    private Set<OAuthScope> scopes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "client_settings", length = 2000, nullable = false)
    private OAuthClientSettings clientSettings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "token_settings", length = 2000, nullable = false)
    private OAuthTokenSettings tokenSettings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthClientStatus status;
}