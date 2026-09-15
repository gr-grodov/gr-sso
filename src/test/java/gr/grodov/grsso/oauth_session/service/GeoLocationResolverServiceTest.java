package gr.grodov.grsso.oauth_session.service;

import com.maxmind.geoip2.DatabaseReader;
import gr.grodov.grsso.oauth_session.props.GeoIpAppProperties;
import gr.grodov.grsso.oauth_session.service.dto.GeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoLocationResolverServiceTest {

    @Mock
    private GeoIpAppProperties properties;
    @Mock
    private GeoIpDatabaseReaderFactory readerFactory;
    @Mock
    private DatabaseReader reader;

    @Mock
    private CityResponse cityResponse;
    @Mock
    private Country country;
    @Mock
    private City city;

    private Path databasePath;
    private GeoLocationResolverService service;

    @BeforeEach
    void setUp() throws Exception {
        databasePath = Files.createTempFile("GeoIP", ".mmdb");
        when(properties.databasePath()).thenReturn(databasePath);
        when(readerFactory.create(databasePath)).thenReturn(reader);
        service = new GeoLocationResolverService(properties, readerFactory);
    }

    @Test
    void resolve_withExistGeoLocation_returnGeoLocation() throws Exception {
        when(reader.city(InetAddress.getByName("127.0.0.1"))).thenReturn(cityResponse);
        when(cityResponse.country()).thenReturn(country);
        when(cityResponse.city()).thenReturn(city);
        when(country.name()).thenReturn("COUNTRY");
        when(city.name()).thenReturn("CITY");

        var result = service.resolve("127.0.0.1");

        assertThat(result).isEqualTo(new GeoLocation("COUNTRY", "CITY"));
        verify(reader).city(InetAddress.getByName("127.0.0.1"));
    }

    @Test
    void resolve_withNoExistGeoLocation_returnUnknownGeoLocation() throws Exception {
        when(reader.city(InetAddress.getByName("127.0.0.1"))).thenThrow(AddressNotFoundException.class);

        var result = service.resolve("127.0.0.1");

        assertThat(result).isEqualTo(GeoLocation.unknown());
    }

    @Test
    void resolve_withException_returnUnknownGeoLocation() throws Exception {
        when(reader.city(InetAddress.getByName("127.0.0.1"))).thenThrow(RuntimeException.class);

        var result = service.resolve("127.0.0.1");

        assertThat(result).isEqualTo(GeoLocation.unknown());
    }
}