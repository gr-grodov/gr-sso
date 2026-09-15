package gr.grodov.grsso.attachment.storage.webdav;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import gr.grodov.grsso.attachment.props.StorageAppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDavConfig {
    @Bean
    public Sardine sardine(StorageAppProperties properties) {
        return SardineFactory.begin(properties.webdav().username(), properties.webdav().password());
    }
}
