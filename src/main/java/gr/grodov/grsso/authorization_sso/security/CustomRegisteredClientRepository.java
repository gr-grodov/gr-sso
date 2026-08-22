package gr.grodov.grsso.authorization_sso.security;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthClient;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientStatus;
import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.oauth_client.domain.repo.OAuthClientRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final OAuthClientRepo oAuthClientRepo;
    private final Mapper<OAuthClient, RegisteredClient> registeredClientMapper;

    @Override
    public void save(@NonNull RegisteredClient registeredClient) {
        oAuthClientRepo.save(registeredClientMapper.toDB(registeredClient));
    }

    @Override
    public @Nullable RegisteredClient findById(@NonNull String id) {
        return oAuthClientRepo.findByIdAndStatus(id, OAuthClientStatus.ACTIVE)
            .map(registeredClientMapper::fromDB)
            .orElse(null);
    }

    @Override
    public @Nullable RegisteredClient findByClientId(@NonNull String clientId) {
        return oAuthClientRepo.findByClientIdAndStatus(clientId, OAuthClientStatus.ACTIVE)
            .map(registeredClientMapper::fromDB)
            .orElse(null);
    }
}
