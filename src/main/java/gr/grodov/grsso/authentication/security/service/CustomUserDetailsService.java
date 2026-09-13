package gr.grodov.grsso.authentication.security.service;

import gr.grodov.grsso.user.service.dto.UserInfoDto;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.authentication.security.principal.UserPrincipal;
import gr.grodov.grsso.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws AccountStatusException {
        UserInfoDto user = userInfoService.findByEmailAndProvider(username, AuthProvider.LOCAL);
        if (!user.enabled()) {
            throw new DisabledException("User %s is not enabled".formatted(username));
        }
        return UserPrincipal.local(user);
    }
}
