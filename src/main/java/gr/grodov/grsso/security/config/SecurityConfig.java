package gr.grodov.grsso.security.config;

import gr.grodov.grsso.security.handler.AuthFailureHandler;
import gr.grodov.grsso.security.handler.OAuth2FailureHandler;
import gr.grodov.grsso.security.service.CustomOAuthUserService;
import gr.grodov.grsso.security.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.ClientSecretAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        CustomOAuthUserService oAuthUserService,
        CustomOidcUserService oidcUserService,
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
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login").permitAll()
                .failureHandler(oAuth2FailureHandler)
                .userInfoEndpoint(config -> config
                    .userService(oAuthUserService)
                    .oidcUserService(oidcUserService)
                )
            )
//            .csrf(csrf ->
//                csrf.ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher())
//            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .oauth2AuthorizationServer((authorizationServer) -> authorizationServer
                .oidc(Customizer.withDefaults())
            );

        return http.build();

    }

//    @Bean
//    RegisteredClientRepository registeredClientRepository() {
//        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
//                .clientId("crm-client")
//                .clientSecret("{noop}secret")
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/grsso")
//                .scope("openid")
//                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
//                .build();
//
//        return new InMemoryRegisteredClientRepository(registeredClient);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("sha256", new StandardPasswordEncoder());
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
}
