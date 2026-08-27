package gr.grodov.grsso.authorization_sso.security;

import gr.grodov.grsso.authorization_sso.domain.entity.OAuthConsent;
import gr.grodov.grsso.authorization_sso.domain.entity.OAuthConsentId;
import gr.grodov.grsso.authorization_sso.domain.repo.OAuthConsentRepo;
import gr.grodov.grsso.common.mapper.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2ConsentServiceTest {

    @Autowired
    @Mock
    private OAuthConsentRepo oAuthConsentRepo;
    @Autowired
    @Mock
    private Mapper<OAuthConsent, OAuth2AuthorizationConsent> oAuthConsentMapper;
    @InjectMocks
    private CustomOAuth2ConsentService oAuth2ConsentService;

    @Test
    void save_withConsent_saveInDB() {
        var consent = buildOAuth2AuthorizationConsent("crm-client", "1");

        oAuth2ConsentService.save(consent);

        verify(oAuthConsentRepo).save(any());
    }

    @Test
    void remove_withConsent_deleteFromDB() {
        var consent = buildOAuth2AuthorizationConsent("crm-client", "1");

        oAuth2ConsentService.remove(consent);

        verify(oAuthConsentRepo).delete(any());
    }

    @Test
    void findById_withExistConsent_returnConsent() {
        var oauthConsentId = new OAuthConsentId("crm-client", "1");
        var oauthConsent = OAuthConsent.builder().id(oauthConsentId).build();
        var consent = buildOAuth2AuthorizationConsent("crm-client", "1");
        when(oAuthConsentRepo.findById(oauthConsentId)).thenReturn(Optional.of(oauthConsent));
        when(oAuthConsentMapper.fromDB(any())).thenReturn(consent);

        var result = oAuth2ConsentService.findById("crm-client", "1");

        assertThat(result).isEqualTo(consent);
    }

    @Test
    void findById_noExistConsent_returnNull() {
        when(oAuthConsentRepo.findById(new OAuthConsentId("crm-client", "1"))).thenReturn(Optional.empty());

        var result = oAuth2ConsentService.findById("crm-client", "1");

        assertThat(result).isNull();
    }

    private OAuth2AuthorizationConsent buildOAuth2AuthorizationConsent(String registeredClientId, String principalName) {
        return OAuth2AuthorizationConsent.withId(registeredClientId, principalName)
            .authorities(grantedAuthorities -> grantedAuthorities.add(new SimpleGrantedAuthority("SCOPE_openid")))
            .build();

    }
}