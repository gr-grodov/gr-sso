package gr.grodov.grsso.authorization_sso.security.config;

import gr.grodov.grsso.authorization_sso.security.OAuthClaimsService;
import gr.grodov.grsso.common.jackson.SpecificJsonMapper;
import gr.grodov.grsso.common.props.FrontendAppProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@NamedInterface("security")
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain oauth2FilterChain(
        HttpSecurity http,
        OAuthClaimsService oAuthClaimsService,
        OAuthAuthenticationEntryPoint authAuthenticationEntryPoint,
        FrontendAppProperties properties
    ) {
        http
            .oauth2AuthorizationServer((authorizationServer) -> {
                http
                    .securityMatcher(authorizationServer.getEndpointsMatcher())
                    .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                    )
                    .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authAuthenticationEntryPoint)
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
                    )
                    .authorizationEndpoint(conf -> conf
                        .consentPage(properties.oauthConsentUrl())
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
}
