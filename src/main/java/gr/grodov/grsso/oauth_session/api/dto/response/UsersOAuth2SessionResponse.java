package gr.grodov.grsso.oauth_session.api.dto.response;

import gr.grodov.grsso.oauth_session.service.dto.UserOAuth2SessionDto;

import java.util.List;

public record UsersOAuth2SessionResponse(
    List<UserOAuth2SessionDto> users,
    long countUsers,
    int totalPage
) {
}
