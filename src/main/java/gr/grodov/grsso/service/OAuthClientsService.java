package gr.grodov.grsso.service;

import gr.grodov.grsso.domain.dto.OAuthClientDto;
import gr.grodov.grsso.domain.entities.OAuthClient;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.repo.OAuthClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuthClientsService {
    private final RegisteredClientRepository registeredClientRepository;
    private final OAuthClientRepo oAuthClientRepo;
    private final Mapper<OAuthClient, OAuthClientDto> oauthClientMapper;
    private final Mapper<RegisteredClient, OAuthClientDto> registeredClientMapper;

    @Transactional(readOnly = true)
    public List<OAuthClientDto> list() {
        return oAuthClientRepo.findAll().stream().map(oauthClientMapper::fromDB).toList();
    }

    @Transactional
    public void save(OAuthClientDto client) {
        registeredClientRepository.save(registeredClientMapper.toDB(client));
    }
}
