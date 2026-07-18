package gr.grodov.grsso.service;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.AuthProvider;
import gr.grodov.grsso.domain.entities.Role;
import gr.grodov.grsso.domain.entities.UserInfo;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.repo.UserInfoRepo;
import gr.grodov.grsso.security.exceptions.EmailAlreadyExistsException;
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
    public UserInfoDto findByEmail(String email) throws UsernameNotFoundException {
        return userInfoRepo.findByEmail(email)
            .map(userInfoMapper::fromDB)
            .orElseThrow(() -> new UsernameNotFoundException(email)
        );
    }

    @Transactional
    public UserInfoDto createNewUser(String email, String password, AuthProvider provider) throws EmailAlreadyExistsException {
        if (userInfoRepo.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        return userInfoMapper.fromDB(
            userInfoRepo.save(userInfoMapper.toDB(
                UserInfoDto.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.USER)
                    .provider(provider)
                    .enabled(true)
                .build())
            )
        );
    }
}
