package gr.grodov.grsso.security;

import gr.grodov.grsso.dto.UserInfoDto;
import gr.grodov.grsso.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {

        UserInfoDto user = userInfoService.findByEmail(username);

        return User.withUsername(user.email())
            .password(user.password())
            .roles("USER")
            .build();
    }
}
