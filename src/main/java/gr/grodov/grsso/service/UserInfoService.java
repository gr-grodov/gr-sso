package gr.grodov.grsso.service;

import gr.grodov.grsso.dto.UserInfoDto;
import gr.grodov.grsso.mapper.UserInfoMapper;
import gr.grodov.grsso.repo.UserInfoRepo;
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

    @Transactional(readOnly = true)
    public UserInfoDto findByEmail(String email) throws UsernameNotFoundException {
        return userInfoRepo.findByEmail(email)
            .map(UserInfoMapper::fromDB)
            .orElseThrow(() -> new UsernameNotFoundException(email)
        );
    }

    @Transactional
    public UserInfoDto createNewUser(String email, String password) {
        return UserInfoMapper.fromDB(
            userInfoRepo.save(UserInfoMapper.toDB(
                UserInfoDto.builder()
                    .email(email)
                    .password(
                            passwordEncoder.encode(password))
                    .enabled(true)
                .build())
            )
        );
    }
}
