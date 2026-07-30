package gr.grodov.grsso.domain.mapper;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserInfoMapper implements Mapper<UserInfo, UserInfoDto> {
    @Override
    public UserInfoDto fromDB(UserInfo user) {
        return UserInfoDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .enabled(user.getEnabled())
            .externalId(user.getExternalId())
            .provider(user.getProvider())
            .role(user.getRole())
        .build();
    }

    @Override
    public UserInfo toDB(UserInfoDto user) {
        return UserInfo.builder()
            .id(user.id())
            .email(user.email())
            .password(user.password())
            .enabled(user.enabled())
            .externalId(user.externalId())
            .provider(user.provider())
            .role(user.role())
        .build();
    }
}