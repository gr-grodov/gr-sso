package gr.grodov.grsso.service;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;
import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.AuthProvider;
import gr.grodov.grsso.domain.entities.user.Role;
import gr.grodov.grsso.domain.entities.user.UserInfo;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.repo.UserInfoRepo;
import gr.grodov.grsso.service.exceptions.EmailAlreadyExistsException;
import gr.grodov.grsso.service.exceptions.OAuthClientNotFoundException;
import gr.grodov.grsso.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserInfoRepo userInfoRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Mapper<UserInfo, UserInfoDto> mapper;
    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    void findByEmailAndProvider_withNoFindEmail_throwsUsernameNotFoundException() {
        when(userInfoRepo.findByEmailAndProvider("test@mail.com", AuthProvider.LOCAL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userInfoService.findByEmailAndProvider("test@mail.com", AuthProvider.LOCAL))
            .isInstanceOf(UsernameNotFoundException.class);
        verify(userInfoRepo).findByEmailAndProvider(anyString(), any(AuthProvider.class));
        verifyNoInteractions(mapper, passwordEncoder);
    }

    @Test
    void findByEmailAndProvider_withCorrectEmail_returnUser() {
        var user = new UserInfo();
        var expectDto = UserInfoDto.builder()
            .email("test@mail.com")
            .provider(AuthProvider.LOCAL)
            .build();
        when(userInfoRepo.findByEmailAndProvider("test@mail.com", AuthProvider.LOCAL)).thenReturn(Optional.of(user));
        when(mapper.fromDB(any())).thenReturn(expectDto);

        var result = userInfoService.findByEmailAndProvider("test@mail.com", AuthProvider.LOCAL);

        assertThat(result).isEqualTo(expectDto);
        verify(userInfoRepo).findByEmailAndProvider(anyString(), any(AuthProvider.class));
        verify(mapper).fromDB(any(UserInfo.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void findById_withIncorrectId_throwsUserNotFoundException() {
        String incorrectId = "21321-213123-2131";

        assertThatThrownBy(() -> userInfoService.findById(incorrectId))
            .isInstanceOf(UserNotFoundException.class)
            .satisfies(ex -> {
                var exception = (UserNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("user_not_found");
            });
        verifyNoInteractions(userInfoRepo, mapper, passwordEncoder);
    }

    @Test
    void findById_withEmptyUsers_throwsUserNotFoundException() {
        String id = "1";
        when(userInfoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userInfoService.findById(id))
            .isInstanceOf(UserNotFoundException.class)
            .satisfies(ex -> {
                var exception = (UserNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("user_not_found");
            });
        verify(userInfoRepo).findById(anyLong());
        verifyNoInteractions(mapper, passwordEncoder);
    }

    @Test
    void findById_withCorrectId_returnUser() {
        String id = "1";
        var user = UserInfo.builder()
            .id(1L)
            .email("test@mail.com")
            .provider(AuthProvider.LOCAL)
            .build();
        var expectDto = UserInfoDto.builder()
            .id(1L)
            .email("test@mail.com")
            .provider(AuthProvider.LOCAL)
            .build();
        when(userInfoRepo.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.fromDB(any(UserInfo.class))).thenReturn(expectDto);

        var result = userInfoService.findById(id);

        assertThat(result).isEqualTo(expectDto);
        verify(userInfoRepo).findById(anyLong());
        verify(mapper).fromDB(any(UserInfo.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createNewUser_withAlreadySavedEmail_throwsEmailAlreadyExistsException() {
        when(userInfoRepo.existsByEmailAndProvider("test@mail.com", AuthProvider.LOCAL)).thenReturn(true);
        var expectErrorFiled = new ErrorFieldDto("email", "already_exist");

        assertThatThrownBy(() -> userInfoService.createNewUser("test@mail.com", "", AuthProvider.LOCAL))
            .isInstanceOf(EmailAlreadyExistsException.class)
            .satisfies(ex -> {
                var exception = (EmailAlreadyExistsException) ex;
                assertThat(exception.getCode()).isEqualTo("email_invalid");
                assertThat(exception.getErrorsField().getFirst()).isEqualTo(expectErrorFiled);
            });
        verify(userInfoRepo, never()).save(any());
        verifyNoInteractions(passwordEncoder, mapper);
    }

    @Test
    void createNewUser_withCorrectEmail_returnSave() {
        var user = new UserInfo();
        var expectDto = UserInfoDto.builder()
            .id(1L)
            .email("test@mail.com")
            .provider(AuthProvider.LOCAL)
            .build();
        when(userInfoRepo.existsByEmailAndProvider("test@mail.com", AuthProvider.LOCAL)).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encode-password");
        when(mapper.toDB(any(UserInfoDto.class))).thenReturn(user);
        when(userInfoRepo.save(user)).thenReturn(user);
        when(mapper.fromDB(user)).thenReturn(expectDto);

        userInfoService.createNewUser("test@mail.com","password",  AuthProvider.LOCAL);

        ArgumentCaptor<UserInfoDto> captor = ArgumentCaptor.forClass(UserInfoDto.class);
        verify(mapper).toDB(captor.capture());
        var result = captor.getValue();
        assertThat(result.role()).isEqualTo(Role.USER);
        assertThat(result.provider()).isEqualTo(AuthProvider.LOCAL);
        assertThat(result.email()).isEqualTo("test@mail.com");
        assertThat(result.password()).isNotEqualTo("password");
        assertThat(result.enabled()).isTrue();
        verify(userInfoRepo).save(any());
        verify(passwordEncoder).encode(anyString());
    }
}