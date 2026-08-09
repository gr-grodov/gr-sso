package gr.grodov.grsso.cache.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthAuthorizationRequest {
    private String requestUri;
    private Instant createdAt;
}
