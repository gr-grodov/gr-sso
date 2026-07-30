package gr.grodov.grsso.service;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.AuthProvider;
import gr.grodov.grsso.domain.entities.user.Role;
import gr.grodov.grsso.domain.entities.user.UserInfo;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.repo.UserInfoRepo;
import gr.grodov.grsso.service.exceptions.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepo userInfoRepo;
    private final PasswordEncoder passwordEncoder;
    private final Mapper<UserInfo, UserInfoDto> userInfoMapper;

    @Transactional(readOnly = true)
    public UserInfoDto findByUserInfo(String email, AuthProvider provider) throws UsernameNotFoundException {
        return userInfoRepo.findByEmailAndProvider(email, provider)
            .map(userInfoMapper::fromDB)
            .orElseThrow(() -> new UsernameNotFoundException(email)
        );
    }

    @Transactional(readOnly = true)
    public boolean existUser(String email) throws UsernameNotFoundException {
        return userInfoRepo.existsByEmail(email);
    }

    @Transactional
    public UserInfoDto createNewUser(String email, String password, AuthProvider provider) throws EmailAlreadyExistsException {
        if (userInfoRepo.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("email_invalid", "email", "already_exist");
        }

        return userInfoMapper.fromDB(
            userInfoRepo.save(userInfoMapper.toDB(
                UserInfoDto.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .provider(provider)
                    .enabled(true)
                .build())
            )
        );
    }
}
