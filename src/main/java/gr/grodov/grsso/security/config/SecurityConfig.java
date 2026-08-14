package gr.grodov.grsso.security.config;

import gr.grodov.grsso.domain.entities.user.Role;
import gr.grodov.grsso.props.FrontendAppProperties;
import gr.grodov.grsso.security.entrypoint.ApiAuthenticationEntryPoint;
import gr.grodov.grsso.security.entrypoint.OAuthAuthenticationEntryPoint;
import gr.grodov.grsso.security.handler.ApiAccessDeniedHandler;
import gr.grodov.grsso.security.handler.AuthLogoutSuccessHandler;
import gr.grodov.grsso.security.handler.OAuthFailureHandler;
import gr.grodov.grsso.security.handler.OAuthSuccessHandler;
import gr.grodov.grsso.security.principal.jackson.UserPrincipalJacksonModule;
import gr.grodov.grsso.security.service.auth.CustomOAuthUserService;
import gr.grodov.grsso.security.service.auth.CustomOidcUserService;
import gr.grodov.grsso.security.principal.UserPrincipal;
import gr.grodov.grsso.security.service.oauth2.OAuthClaimsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain oauthFilterChain(
        HttpSecurity http,
        CustomOAuthUserService oAuthUserService,
        CustomOidcUserService oidcUserService,
        OAuthFailureHandler oAuthFailureHandler,
        ApiAccessDeniedHandler apiAccessDeniedHandler,
        ApiAuthenticationEntryPoint authenticationEntryPoint,
        OAuthSuccessHandler oAuthSuccessHandler,
        AuthLogoutSuccessHandler authLogoutSuccessHandler,
        FrontendAppProperties frontendProperties
    ) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/config/**", "/error/**").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/register").anonymous()
                .requestMatchers("/api/admin/**").hasAuthority(Role.ADMIN.getAuthority())
                .anyRequest().authenticated()
            )
            .csrf((csrf) -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .cors(Customizer.withDefaults())
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(config -> config
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("SSO_SESSION", "XSRF-TOKEN")
                .logoutSuccessHandler(authLogoutSuccessHandler)
                .logoutSuccessUrl(frontendProperties.loginUrl())
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(apiAccessDeniedHandler)
            )
            .oauth2Login(oauth -> oauth
                .failureHandler(oAuthFailureHandler)
                .successHandler(oAuthSuccessHandler)
                .userInfoEndpoint(config -> config
                    .userService(oAuthUserService)
                    .oidcUserService(oidcUserService)
                )
            );

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain ssoFilterChain(
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
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
