package gr.grodov.grsso.infrastructure.security;

import gr.grodov.grsso.common.props.FrontendAppProperties;
import gr.grodov.grsso.infrastructure.security.filters.DeviceIdFilter;
import gr.grodov.grsso.user.domain.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(3)
    public SecurityFilterChain apiFilterChain(
        HttpSecurity http,
        DeviceIdFilter deviceIdFilter,
        ApiAuthenticationEntryPoint apiAuthenticationEntryPoint,
        ApiAccessDeniedHandler apiAccessDeniedHandler,
        AuthLogoutSuccessHandler authLogoutSuccessHandler,
        FrontendAppProperties frontendProperties
    ) {
        http
            .addFilterBefore(deviceIdFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/config/**", "/error/**").permitAll()
                .requestMatchers("/api/auth/**").anonymous()
                .requestMatchers("/api/admin/**").hasAuthority(Role.ADMIN.getAuthority())
                .anyRequest().authenticated()
            )
            .csrf((csrf) -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(config -> config
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessHandler(authLogoutSuccessHandler)
                .logoutSuccessUrl(frontendProperties.loginUrl())
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(apiAuthenticationEntryPoint)
                .accessDeniedHandler(apiAccessDeniedHandler)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
