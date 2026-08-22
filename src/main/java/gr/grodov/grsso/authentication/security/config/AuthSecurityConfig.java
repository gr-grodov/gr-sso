package gr.grodov.grsso.authentication.security.config;

import gr.grodov.grsso.authentication.security.service.CustomOAuthUserService;
import gr.grodov.grsso.authentication.security.service.CustomOidcUserService;
import gr.grodov.grsso.authentication.security.handler.OAuthFailureHandler;
import gr.grodov.grsso.authentication.security.handler.OAuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class AuthSecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain authFilterChain(
        HttpSecurity http,
        OAuthSuccessHandler oAuthSuccessHandler,
        OAuthFailureHandler oAuthFailureHandler,
        CustomOAuthUserService oAuthUserService,
        CustomOidcUserService oidcUserService
    ) {
        http
            .securityMatcher("/oauth2/authorization/**", "/login/oauth2/code/**")
            .oauth2Login(oauth -> oauth
                .successHandler(oAuthSuccessHandler)
                .failureHandler(oAuthFailureHandler)
                .userInfoEndpoint(config -> config
                    .userService(oAuthUserService)
                    .oidcUserService(oidcUserService)
                )
            );

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}
