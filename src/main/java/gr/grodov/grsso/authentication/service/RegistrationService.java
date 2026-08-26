package gr.grodov.grsso.authentication.service;

import gr.grodov.grsso.authentication.api.dto.request.RegistrationRequest;
import gr.grodov.grsso.authentication.api.dto.response.RegistrationResponse;
import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserInfoService userInfoService;
    private final VerifyEmailService verifyEmailService;

    @Transactional
    public RegistrationResponse registration(RegistrationRequest request, HttpServletRequest httpRequest) {
        UserInfoDto userInfo = userInfoService.createNewUser(request.getEmail(), request.getPassword(), AuthProvider.LOCAL);

        Locale locale = RequestContextUtils.getLocale(httpRequest);
        String verifyId = verifyEmailService.sendVerifyCode(userInfo, locale);

        return new RegistrationResponse(userInfo.email(), verifyId);
    }
}
