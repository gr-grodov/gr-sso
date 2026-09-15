package gr.grodov.grsso.oauth_session.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import gr.grodov.grsso.oauth_session.props.GeoIpAppProperties;
import gr.grodov.grsso.oauth_session.service.dto.GeoLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.NamedInterface;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;

@NamedInterface("service")
@Slf4j
@Component
public class GeoLocationResolverService {

    private volatile DatabaseReader reader;

    private final GeoIpAppProperties properties;
    private final GeoIpDatabaseReaderFactory readerFactory;

    public GeoLocationResolverService(
        GeoIpAppProperties properties,
        GeoIpDatabaseReaderFactory readerFactory
    ) throws IOException {
        this.properties = properties;
        this.readerFactory = readerFactory;
        this.reader = loadReader();
    }

    public synchronized void reload() throws IOException {
        DatabaseReader old = this.reader;
        this.reader = loadReader();

        if (old != null) {
            old.close();
        }
    }

    private DatabaseReader loadReader() throws IOException {
        if (!Files.exists(properties.databasePath())) {
            log.warn("GeoIP database not found at {}", properties.databasePath());
            return null;
        }

        return readerFactory.create(properties.databasePath());
    }

    public GeoLocation resolve(String ipAddress) {
        if (reader == null) {
            log.warn("GeoIP database not found");
            return GeoLocation.unknown();
        }

        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            CityResponse response = reader.city(address);

            return new GeoLocation(
                response.country().name(),
                response.city().name()
            );
        } catch (AddressNotFoundException ex) {
            return GeoLocation.unknown();
        } catch (Exception ex) {
            log.warn("Failed to resolve geolocation for IP", ex);
            return GeoLocation.unknown();
        }
    }
}
