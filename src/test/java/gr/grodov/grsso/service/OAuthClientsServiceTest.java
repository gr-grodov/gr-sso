package gr.grodov.grsso.service;

import gr.grodov.grsso.oauth_client.api.dto.request.OAuthClientChangeStatusRequest;
import gr.grodov.grsso.oauth_client.api.dto.request.OAuthClientRequest;
import gr.grodov.grsso.common.api.ErrorFieldDto;
import gr.grodov.grsso.oauth_client.domain.dto.OAuthClientDto;
import gr.grodov.grsso.oauth_client.domain.dto.OAuthClientShortDto;
import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.oauth_client.domain.repo.OAuthClientRepo;
import gr.grodov.grsso.oauth_client.domain.entity.*;
import gr.grodov.grsso.oauth_client.service.OAuthClientsService;
import gr.grodov.grsso.oauth_client.exception.OAuthClientInvalidException;
import gr.grodov.grsso.oauth_client.exception.OAuthClientNotFoundException;
import gr.grodov.grsso.oauth_client.service.OAuthClientPropertiesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthClientsServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private OAuthClientRepo repo;
    @Mock
    private Mapper<OAuthClient, OAuthClientDto> clientMapper;
    @Mock
    private Mapper<OAuthClient, OAuthClientShortDto> clientShortMapper;
    @Mock
    private OAuthClientPropertiesService propertiesService;
    private OAuthClientsService service;

    @BeforeEach
    void setUp() {
        service = new OAuthClientsService(passwordEncoder, repo, clientMapper, clientShortMapper, propertiesService);
    }

    @Test
    void list_withExistData_returnNoEmptyList() {
        var client1 = OAuthClient.builder().id("1").clientName("crm-api").build();
        var client2 = OAuthClient.builder().id("2").clientName("front-api").build();
        var dto1 = OAuthClientShortDto.builder().id("1").clientName("crm-api").build();
        var dto2 = OAuthClientShortDto.builder().id("2").clientName("front-api").build();
        when(repo.findAllByOrderByUpdatedAtDesc()).thenReturn(List.of(client1, client2));
        when(clientShortMapper.fromDB(client1)).thenReturn(dto1);
        when(clientShortMapper.fromDB(client2)).thenReturn(dto2);

        var result = service.list();

        assertThat(result).isEqualTo(List.of(dto1, dto2));
        verify(repo).findAllByOrderByUpdatedAtDesc();
    }

    @Test
    void save_withExistClientName_throwsOAuthClientInvalidException() {
        var request = new OAuthClientRequest();
        request.setClientName("crm");
        when(repo.existsByClientName("crm")).thenReturn(true);
        var expectErrorField = new ErrorFieldDto("clientName", "exists");

        assertThatThrownBy(() -> service.save(request))
            .isInstanceOf(OAuthClientInvalidException.class)
            .satisfies(ex -> {
                var exception = (OAuthClientInvalidException) ex;
                assertThat(exception.getErrorsField().getFirst()).isEqualTo(expectErrorField);
            });

        verify(repo).existsByClientName(anyString());
        verify(repo, never()).save(any());
    }


    @Test
    void save_withNoExistClientName_returnOAuthClientSecretInfoResponse() {
        var request = new OAuthClientRequest();
        request.setClientName("crm");
        request.setRedirectUris(Set.of("http://example.com/oauth/code"));
        request.setScopes(Set.of(OAuthScope.OPEN_ID));
        request.setAuthorizationGrantTypes(Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE));
        request.setClientAuthenticationMethods(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC));
        when(repo.existsByClientName("crm")).thenReturn(false);
        when(propertiesService.filterAuthorizationGrantTypes(anySet())).thenReturn(Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE));
        when(propertiesService.filterAuthenticationMethods(anySet())).thenReturn(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC));

        ArgumentCaptor<OAuthClient> captor = ArgumentCaptor.forClass(OAuthClient.class);
        var result = service.save(request);

        verify(repo).save(captor.capture());
        var savedClient = captor.getValue();
        assertThat(savedClient.getClientName()).isEqualTo("crm");
        assertThat(savedClient.getRedirectUris()).isEqualTo(Set.of("http://example.com/oauth/code"));
        assertThat(savedClient.getScopes()).isEqualTo(Set.of(OAuthScope.OPEN_ID));
        assertThat(savedClient.getAuthorizationGrantTypes()).isEqualTo(Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE));
        assertThat(savedClient.getClientAuthenticationMethods()).isEqualTo(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC));
        assertThat(savedClient.getClientId()).isEqualTo(result.clientID());
        assertThat(savedClient.getStatus()).isEqualTo(OAuthClientStatus.ACTIVE);
        assertThat(savedClient.getClientSecret()).isNotEqualTo(result.clientSecret());
    }

    @Test
    void edit_withNoExistClient_throwsOAuthClientNotFoundException() {
        var request = new OAuthClientRequest();
        request.setId("1");
        when(repo.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.edit(request))
            .isInstanceOf(OAuthClientNotFoundException.class)
            .satisfies(ex -> {
                var exception = (OAuthClientNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("oauth_client_not_found");
            });
        verify(repo, never()).save(any());
    }

    @Test
    void edit_withCorrectRequest_editOAuthClient() {
        var request = new OAuthClientRequest();
        request.setId("1");
        request.setClientName("crm");
        request.setRedirectUris(Set.of("http://example.com/oauth/code"));
        request.setScopes(Set.of(OAuthScope.OPEN_ID));
        request.setAuthorizationGrantTypes(Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE));
        request.setClientAuthenticationMethods(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC));
        var client = OAuthClient.builder()
            .id("1")
            .clientName("crm-api")
            .redirectUris(Set.of("http://crm-api.ru/oauth/code"))
            .scopes(Set.of(OAuthScope.OPEN_ID, OAuthScope.EMAIL))
            .authorizationGrantTypes(Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE, OAuthAuthorizationGrantType.REFRESH_TOKEN))
            .clientAuthenticationMethods(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC))
        .build();
        when(repo.findById("1")).thenReturn(Optional.of(client));
        when(propertiesService.filterAuthorizationGrantTypes(anySet())).thenReturn(Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE));
        when(propertiesService.filterAuthenticationMethods(anySet())).thenReturn(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC));

        ArgumentCaptor<OAuthClient> captor = ArgumentCaptor.forClass(OAuthClient.class);
        service.edit(request);

        verify(repo).save(captor.capture());
        var savedClient = captor.getValue();
        assertThat(savedClient.getId()).isEqualTo("1");
        assertThat(savedClient.getClientName()).isEqualTo("crm");
        assertThat(savedClient.getRedirectUris()).isEqualTo(Set.of("http://example.com/oauth/code"));
        assertThat(savedClient.getScopes()).isEqualTo(Set.of(OAuthScope.OPEN_ID));
        assertThat(savedClient.getAuthorizationGrantTypes()).isEqualTo(Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE));
        assertThat(savedClient.getClientAuthenticationMethods()).isEqualTo(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC));
    }

    @Test
    void getById_withNotExistId_throwsOAuthClientNotFoundException() {
        when(repo.findById("2")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("2"))
            .isInstanceOf(OAuthClientNotFoundException.class)
            .satisfies(ex -> {
                var exception = (OAuthClientNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("oauth_client_not_found");
            });
    }

    @Test
    void getById_withCorrectId_returnOAuthClientDto() {
        var client = OAuthClient.builder()
            .id("1")
            .clientName("crm-api")
        .build();
        var expectDto = OAuthClientDto.builder()
            .id("1")
            .clientName("crm-api")
        .build();
        when(repo.findById("1")).thenReturn(Optional.of(client));
        when(clientMapper.fromDB(client)).thenReturn(expectDto);

        var result = service.getById("1");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.clientName()).isEqualTo("crm-api");
    }

    @Test
    void getByClientId_withNotExistsClientId_throwsOAuthClientNotFoundException() {
        when(repo.findByClientId("crm-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByClientId("crm-123"))
            .isInstanceOf(OAuthClientNotFoundException.class)
            .satisfies(ex -> {
                var exception = (OAuthClientNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("oauth_client_not_found");
            });
    }

    @Test
    void getByClientId_withCorrectClientId_returnOAuthClientDto() {
        var client = OAuthClient.builder()
            .id("1")
            .clientId("crm-api-123")
        .build();
        var expectDto = OAuthClientDto.builder()
            .id("1")
            .clientId("crm-api-123")
        .build();
        when(repo.findByClientId("crm-api-123")).thenReturn(Optional.of(client));
        when(clientMapper.fromDB(client)).thenReturn(expectDto);

        var result = service.getByClientId("crm-api-123");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.clientId()).isEqualTo("crm-api-123");
    }

    @Test
    void changeStatus_withNotExistsId_throwsOAuthClientNotFoundException() {
        var request = new OAuthClientChangeStatusRequest("1", OAuthClientStatus.DISABLED);
        when(repo.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(request))
            .isInstanceOf(OAuthClientNotFoundException.class)
            .satisfies(ex -> {
                var exception = (OAuthClientNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("oauth_client_not_found");
            });

        verify(repo, never()).save(any());
    }

    @Test
    void changeStatus_withCorrectId_returnOAuthClientShortDto() {
        var request = new OAuthClientChangeStatusRequest("1", OAuthClientStatus.DISABLED);
        var client = OAuthClient.builder()
            .id("1")
            .clientId("crm-api-123")
            .status(OAuthClientStatus.ACTIVE)
        .build();
        var expectDto = OAuthClientShortDto.builder()
            .id("1")
            .clientId("crm-api-123")
            .status(OAuthClientStatus.DISABLED)
        .build();
        when(repo.findById("1")).thenReturn(Optional.of(client));
        when(clientShortMapper.fromDB(client)).thenReturn(expectDto);

        ArgumentCaptor<OAuthClient> captor = ArgumentCaptor.forClass(OAuthClient.class);
        var result = service.changeStatus(request);

        verify(repo).save(captor.capture());
        var savedClient = captor.getValue();
        assertThat(savedClient.getStatus()).isEqualTo(OAuthClientStatus.DISABLED);
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.clientId()).isEqualTo("crm-api-123");
    }

    @Test
    void delete_withNotExistsId_throwsOAuthClientNotFoundException() {
        when(repo.existsById("1")).thenReturn(false);

        assertThatThrownBy(() -> service.delete("1"))
            .isInstanceOf(OAuthClientNotFoundException.class)
            .satisfies(ex -> {
                var exception = (OAuthClientNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("oauth_client_not_found");
            });
        verify(repo, never()).delete(any());
        verify(repo, never()).deleteById(anyString());
    }

    @Test
    void delete_withCorrectId_callRepoDelete() {
        when(repo.existsById("1")).thenReturn(true);

        service.delete("1");

        verify(repo).deleteById("1");
    }
}