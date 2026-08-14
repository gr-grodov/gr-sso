package gr.grodov.grsso.security.service.auth;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.AuthProvider;
import gr.grodov.grsso.security.principal.UserPrincipal;
import gr.grodov.grsso.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        UserInfoDto user = userInfoService.findByEmail(username, AuthProvider.LOCAL);
        return UserPrincipal.local(user);
    }
}
