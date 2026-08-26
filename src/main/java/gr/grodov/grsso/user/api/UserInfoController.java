package gr.grodov.grsso.user.api;

import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping
    public UserInfoDto getUserInfo(Principal principal) {
        return userInfoService.findById(principal.getName());
    }
}
