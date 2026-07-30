package gr.grodov.grsso.security.service;

import gr.grodov.grsso.domain.entities.oauth.OAuthClient;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.repo.OAuthClientRepo;
import gr.grodov.grsso.service.OAuthClientsService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final Mapper<OAuthClient, RegisteredClient> registeredClientMapper;
    private final OAuthClientsService oAuthClientsService;

    private final OAuthClientRepo oAuthClientRepo;

    @Override
    public void save(@NonNull RegisteredClient registeredClient) {
        oAuthClientRepo.save(registeredClientMapper.toDB(registeredClient));
    }

    @Override
    public @Nullable RegisteredClient findById(@NonNull String id) {
        return oAuthClientRepo.findById(id).map(registeredClientMapper::fromDB).orElse(null);
    }

    @Override
    public @Nullable RegisteredClient findByClientId(@NonNull String clientId) {
        return oAuthClientRepo.findByClientId(clientId).map(registeredClientMapper::fromDB).orElse(null);
    }
}
