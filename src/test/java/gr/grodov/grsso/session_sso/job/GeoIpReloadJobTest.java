package gr.grodov.grsso.session_sso.job;

import gr.grodov.grsso.session_sso.service.GeoLocationResolverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GeoIpReloadJobTest {

    @Mock
    private GeoLocationResolverService resolverService;
    @InjectMocks
    private GeoIpReloadJob geoIpReloadJob;

    @Test
    void deleteExpiredUnconfirmedUsers_callReload() throws IOException {
        geoIpReloadJob.deleteExpiredUnconfirmedUsers();

        verify(resolverService).reload();
    }
}