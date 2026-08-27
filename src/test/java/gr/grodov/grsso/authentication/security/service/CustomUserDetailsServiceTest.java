package gr.grodov.grsso.authentication.security.service;

import gr.grodov.grsso.authentication.security.principal.UserPrincipal;
import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.domain.entity.Role;
import gr.grodov.grsso.user.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Autowired
    @Mock
    private UserInfoService userInfoService;
    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_withCorrectData_getUserPrincipal() {
        UserInfoDto existingUser = UserInfoDto.builder()
            .id(1L)
            .password("Password123!")
            .email("user@test.com")
            .role(Role.USER)
            .enabled(true)
        .build();
        when(userInfoService.findByEmailAndProvider("user@test.com", AuthProvider.LOCAL)).thenReturn(existingUser);

        UserDetails result = userDetailsService.loadUserByUsername("user@test.com");

        assertThat(result).isExactlyInstanceOf(UserPrincipal.class);
    }

    @Test
    void loadUserByUsername_noExistUsest_throwsUsernameNotFoundException() {
        when(userInfoService.findByEmailAndProvider("user@test.com", AuthProvider.LOCAL)).thenThrow(UsernameNotFoundException.class);

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("user@test.com"))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsername_withDisabledUser_throwsDisabledException() {
        UserInfoDto existingUser = UserInfoDto.builder()
            .id(1L)
            .password("Password123!")
            .email("user@test.com")
            .role(Role.USER)
            .enabled(false)
            .build();
        when(userInfoService.findByEmailAndProvider("user@test.com", AuthProvider.LOCAL)).thenReturn(existingUser);

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("user@test.com"))
            .isInstanceOf(DisabledException.class);
    }
}