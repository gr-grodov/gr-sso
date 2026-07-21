package gr.grodov.grsso.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("grsso")
public record AppProperties(
   String frontUrl
) {
}
