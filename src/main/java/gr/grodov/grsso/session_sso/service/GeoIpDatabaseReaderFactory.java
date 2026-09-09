package gr.grodov.grsso.session_sso.service;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class GeoIpDatabaseReaderFactory {

    public DatabaseReader create(Path path) throws IOException {
        return new DatabaseReader.Builder(path.toFile())
            .withCache(new CHMCache())
            .build();
    }
}