package gr.grodov.grsso.authorization_sso.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import gr.grodov.grsso.authorization_sso.security.OAuthClaimsService;
import gr.grodov.grsso.common.jackson.SpecificJsonMapper;
import gr.grodov.grsso.common.props.FrontendAppProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.oidc.web.OidcProviderConfigurationEndpointFilter;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.json.JsonMapper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain oauth2FilterChain(
        HttpSecurity http,
        OAuthClaimsService oAuthClaimsService,
        OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint,
        OAuth2TokenSuccessHandler oAuth2TokenSuccessHandler,
        OAuth2AuthorizationSuccessHandler oAuth2AuthorizationSuccessHandler,
        FrontendAppProperties properties
    ) {
        //OidcProviderConfigurationEndpointFilter
        http
            .oauth2AuthorizationServer((authorizationServer) -> {
                http
                    .securityMatcher(authorizationServer.getEndpointsMatcher())
                    .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                    )
                    .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(oAuth2AuthenticationEntryPoint)
                    );

                authorizationServer
                    .oidc(oidc -> oidc
                        .userInfoEndpoint(endpoint -> endpoint
                            .userInfoMapper(context -> {
                                Map<String, Object> claims = oAuthClaimsService.userInfoClaims(context);
                                return OidcUserInfo.builder()
                                    .claims(map -> map.putAll(claims))
                                    .build();
                            })
                        )
                        .providerConfigurationEndpoint(config ->
                            config.providerConfigurationCustomizer(builder -> {
                                builder.claim("backchannel_logout_supported", true);
                                builder.claim("backchannel_logout_session_supported", true);
                            })
                        )
                    )
                    .authorizationEndpoint(conf -> conf
                        .consentPage(properties.oauthConsentUrl())
                        .authorizationResponseHandler(oAuth2AuthorizationSuccessHandler)
                    )
                    .tokenEndpoint(conf -> conf
                        .accessTokenResponseHandler(oAuth2TokenSuccessHandler)
                    );
            });

        return http.build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(OAuthClaimsService oAuthClaimsService) {
        return context -> {
            Map<String, Object> claims = oAuthClaimsService.tokenClaims(context);
            context.getClaims().claims(
                map -> map.putAll(claims)
            );
        };
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(
        @Qualifier("authentication_json_mapper") SpecificJsonMapper specificJsonMapper,
        JdbcOperations jdbc,
        RegisteredClientRepository clientRepository
    ) {
        JsonMapper mapper = specificJsonMapper.getJsonMapper();
        JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbc, clientRepository);

        service.setAuthorizationParametersMapper(new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationParametersMapper(mapper));
        service.setAuthorizationRowMapper(new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationRowMapper(clientRepository, mapper));

        return service;
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
