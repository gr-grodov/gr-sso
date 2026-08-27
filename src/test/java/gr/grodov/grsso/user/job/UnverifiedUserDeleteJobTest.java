package gr.grodov.grsso.user.job;

import gr.grodov.grsso.common.props.EmailAppProperties;
import gr.grodov.grsso.user.domain.repo.UserInfoRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnverifiedUserDeleteJobTest {

    @Autowired
    @Mock
    private EmailAppProperties appProperties;
    @Mock
    private UserInfoRepo userInfoRepo;
    @InjectMocks
    private UnverifiedUserDeleteJob deleteJob;

    @Test
    void deleteExpiredUnconfirmedUsers_callDelete() {
        var verifyEmailCode = mock(EmailAppProperties.VerifyEmailCode.class);
        when(appProperties.verifyEmailCode()).thenReturn(verifyEmailCode);
        when(verifyEmailCode.minuteTime()).thenReturn(15);

        deleteJob.deleteExpiredUnconfirmedUsers();

        verify(userInfoRepo).deleteByEnabledFalseAndCreatedAtBefore(any());
    }
}