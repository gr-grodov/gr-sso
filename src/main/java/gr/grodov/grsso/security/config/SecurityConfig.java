package gr.grodov.grsso.security.config;

import gr.grodov.grsso.domain.entities.user.Role;
import gr.grodov.grsso.security.entrypoint.ApiAuthenticationEntryPoint;
import gr.grodov.grsso.security.entrypoint.OAuthAuthenticationEntryPoint;
import gr.grodov.grsso.security.handler.ApiAccessDeniedHandler;
import gr.grodov.grsso.security.handler.OAuth2FailureHandler;
import gr.grodov.grsso.security.handler.OAuth2SuccessHandler;
import gr.grodov.grsso.security.jackson.UserPrincipalJacksonModule;
import gr.grodov.grsso.security.service.CustomOAuthUserService;
import gr.grodov.grsso.security.service.CustomOidcUserService;
import gr.grodov.grsso.security.service.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain oauthFilterChain(
        HttpSecurity http,
        CustomOAuthUserService oAuthUserService,
        CustomOidcUserService oidcUserService,
        OAuth2FailureHandler oAuth2FailureHandler,
        ApiAccessDeniedHandler apiAccessDeniedHandler,
        ApiAuthenticationEntryPoint authenticationEntryPoint,
        OAuth2SuccessHandler oAuth2SuccessHandler
    ) {
        http
            .securityMatcher("/api/**")
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
            .logout(config ->
                config.logoutUrl("/api/auth/logout")
            )
            .logout(logout -> logout
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(apiAccessDeniedHandler)
            )
            .oauth2Login(oauth -> oauth
                .failureHandler(oAuth2FailureHandler)
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint(config -> config
                    .userService(oAuthUserService)
                    .oidcUserService(oidcUserService)
                )
            );

        return http.build();
    }

    @Bean
    public SecurityFilterChain ssoFilterChain(
        HttpSecurity http,
        OAuthAuthenticationEntryPoint authAuthenticationEntryPoint
    ) {
        http
            .securityMatcher("/oauth2/**", "/connect/**", "/.well-known/**", "/userinfo")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authAuthenticationEntryPoint)
            )
            .oauth2AuthorizationServer((authorizationServer) -> authorizationServer
                .oidc(Customizer.withDefaults())
            );

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler();
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
