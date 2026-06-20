package gr.grodov.grsso.mapper;

import gr.grodov.grsso.dto.UserInfoDto;
import gr.grodov.grsso.models.UserInfo;

public class UserInfoMapper {
    public static UserInfoDto fromDB(UserInfo user) {
        return new UserInfoDto(user.getId(), user.getEmail(), user.getPassword(), user.getEnabled());
    }

    public static UserInfo toDB(UserInfoDto user) {
        return new UserInfo(user.id(), user.email(), user.password(), user.enabled());
    }
}
