package gr.grodov.grsso.security.config;

import gr.grodov.grsso.security.handler.AuthFailureHandler;
import gr.grodov.grsso.security.handler.OAuth2FailureHandler;
import gr.grodov.grsso.security.service.CustomOAuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        CustomOAuthUserService oAuthUserService,
        AuthFailureHandler authFailureHandler,
        OAuth2FailureHandler oAuth2FailureHandler
    ) {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                        "/register",
                        "/login-error",
                        "/provider-error",
                        "/css/**", "/js/**", "/images/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login").loginProcessingUrl("/login").permitAll()
                .failureHandler(authFailureHandler)
                .defaultSuccessUrl("/", true)
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login").permitAll()
                .failureHandler(oAuth2FailureHandler)
                .userInfoEndpoint(config ->
                    config.userService(oAuthUserService)
                )
                .defaultSuccessUrl("/", true)
            )
//            .csrf(csrf ->
//                csrf.ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher())
//            )
            .oauth2AuthorizationServer((authorizationServer) -> authorizationServer
                .oidc(Customizer.withDefaults())
            );;

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
