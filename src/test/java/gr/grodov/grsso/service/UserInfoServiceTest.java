package gr.grodov.grsso.service;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.AuthProvider;
import gr.grodov.grsso.domain.entities.user.UserInfo;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.mapper.UserInfoMapper;
import gr.grodov.grsso.domain.repo.UserInfoRepo;
import gr.grodov.grsso.service.exceptions.EmailAlreadyExistsException;
import gr.grodov.grsso.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserInfoServiceTest {

    @Mock
    private UserInfoRepo userInfoRepo;
    private PasswordEncoder passwordEncoder;
    private Mapper<UserInfo, UserInfoDto> mapper;
    private UserInfoService userInfoService;

    @BeforeEach
    void init() {
        userInfoRepo = Mockito.mock(UserInfoRepo.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        mapper = new UserInfoMapper();

        userInfoService = new UserInfoService(userInfoRepo, passwordEncoder, mapper);
    }


    @Test
    void findByEmail_throwsWhenEmailNotFound() {
        when(userInfoRepo.findByEmailAndProvider("test@mail.com", AuthProvider.LOCAL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
           userInfoService.findByEmail("test@mail.com", AuthProvider.LOCAL);
        });
    }

    @Test
    void findByEmail_correctEmail() {
        var user = UserInfo.builder()
            .email("test@mail.com")
            .provider(AuthProvider.LOCAL)
            .build();
        var expectDto = UserInfoDto.builder()
            .email("test@mail.com")
            .provider(AuthProvider.LOCAL)
            .build();
        when(userInfoRepo.findByEmailAndProvider("test@mail.com", AuthProvider.LOCAL)).thenReturn(Optional.of(user));

        var result = userInfoService.findByEmail("test@mail.com", AuthProvider.LOCAL);

        assertEquals(expectDto, result);
    }

    @Test
    void findById_throwsWhenIncorrectId() {
        String incorrectId = "21321-213123-2131";

        assertThrows(UserNotFoundException.class, () -> {
            userInfoService.findById(incorrectId);
        });

        verifyNoInteractions(userInfoRepo);
    }

    @Test
    void findById_throwsWhenNotFound() {
        String id = "1";
        when(userInfoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
           userInfoService.findById(id);
        });
    }

    @Test
    void findById_correctId() {
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

        var result = userInfoService.findById(id);

        assertEquals(expectDto, result);
    }

    @Test
    void createNewUser_throwsWhenEmailAlreadyExist() {
        when(userInfoRepo.existsByEmailAndProvider("test@mail.com", AuthProvider.LOCAL)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userInfoService.createNewUser("test@mail.com", "", AuthProvider.LOCAL);
        });

        verify(userInfoRepo, never()).save(any());
    }
}
