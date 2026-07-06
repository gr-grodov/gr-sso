package gr.grodov.grsso.service;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.AuthProvider;
import gr.grodov.grsso.domain.entities.Role;
import gr.grodov.grsso.domain.mapper.UserInfoMapper;
import gr.grodov.grsso.domain.repo.UserInfoRepo;
import gr.grodov.grsso.security.exceptions.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
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
    public UserInfoDto createNewUser(String email, String password, AuthProvider provider) throws EmailAlreadyExistsException {
        if (userInfoRepo.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        return UserInfoMapper.fromDB(
            userInfoRepo.save(UserInfoMapper.toDB(
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
