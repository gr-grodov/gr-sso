package gr.grodov.grsso.security.config;

import gr.grodov.grsso.domain.entities.user.Role;
import gr.grodov.grsso.security.principal.UserPrincipal;
import gr.grodov.grsso.security.principal.jackson.UserPrincipalJacksonModule;
import gr.grodov.grsso.security.service.oauth2.OAuthClaimsService;
import gr.grodov.grsso.security.service.oauth2.ScopedClaimsBuilder;
import gr.grodov.grsso.service.UserInfoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.util.Map;

@Configuration
public class OAuthConfig {

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
        JdbcOperations jdbc,
        RegisteredClientRepository clientRepository
    ) {
        ClassLoader classLoader = getClass().getClassLoader();
        BasicPolymorphicTypeValidator.Builder validator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(UserPrincipal.class)
            .allowIfSubType(Role.class);

        JsonMapper mapper = JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(classLoader, validator))
            .addModule(new UserPrincipalJacksonModule())
            .build();


        JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbc, clientRepository);

        service.setAuthorizationParametersMapper(new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationParametersMapper(mapper));
        service.setAuthorizationRowMapper(new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationRowMapper(clientRepository, mapper));

        return service;
    }
}
