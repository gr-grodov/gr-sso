package gr.grodov.grsso.user.api;

import gr.grodov.grsso.common.security.AuthPrincipal;
import gr.grodov.grsso.user.api.dto.request.UserProfileInfoRequest;
import gr.grodov.grsso.user.service.dto.UserInfoDto;
import gr.grodov.grsso.user.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping
    public UserInfoDto getUserInfo(@AuthenticationPrincipal AuthPrincipal principal) throws InterruptedException {
        Thread.sleep(1000L);
        return userInfoService.findById(principal.getId());
    }

    @PatchMapping("/profile")
    public UserInfoDto editProfile(
        @AuthenticationPrincipal AuthPrincipal principal,
        @RequestBody @Valid UserProfileInfoRequest profileInfoRequest
    ) {
        return userInfoService.editProfile(principal.getId(), profileInfoRequest);
    }
}
