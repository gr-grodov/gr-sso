package gr.grodov.grsso.user.service;

import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.domain.entity.Role;
import gr.grodov.grsso.user.domain.entity.UserInfo;
import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.user.domain.repo.UserInfoRepo;
import gr.grodov.grsso.user.exception.EmailAlreadyExistsException;
import gr.grodov.grsso.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@NamedInterface("service")
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepo userInfoRepo;
    private final PasswordEncoder passwordEncoder;
    private final Mapper<UserInfo, UserInfoDto> userInfoMapper;

    @Transactional(readOnly = true)
    public UserInfoDto findByEmailAndProvider(String email, AuthProvider provider) throws UsernameNotFoundException {
        return userInfoRepo.findByEmailAndProvider(email, provider)
            .map(userInfoMapper::fromDB)
            .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Transactional(readOnly = true)
    public UserInfoDto findById(String id) throws UserNotFoundException {
        long userId;
        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new UserNotFoundException();
        }

        return userInfoRepo.findById(userId)
            .map(userInfoMapper::fromDB)
            .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public UserInfoDto createNewUser(String email, String password, AuthProvider provider) throws EmailAlreadyExistsException {
        if (userInfoRepo.existsByEmailAndProvider(email, provider)) {
            throw new EmailAlreadyExistsException();
        }

        boolean needVerify = provider == AuthProvider.LOCAL;
        return userInfoMapper.fromDB(
            userInfoRepo.save(userInfoMapper.toDB(
                UserInfoDto.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.USER)
                    .provider(provider)
                    .enabled(!needVerify)
                .build())
            )
        );
    }

    @Transactional
    public void delete(UserInfoDto userInfo) {
        userInfoRepo.delete(userInfoMapper.toDB(userInfo));
    }

    @Transactional
    public UserInfoDto enabledUserInfo(Long userId, boolean enabled) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        userInfo.setEnabled(enabled);

        return userInfoMapper.fromDB(userInfoRepo.save(userInfo));
    }
}
