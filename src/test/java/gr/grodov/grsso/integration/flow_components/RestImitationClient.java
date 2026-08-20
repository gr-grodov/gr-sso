package gr.grodov.grsso.integration.flow_components;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.params.shadow.de.siegmar.fastcsv.util.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Lazy
@Component
@RequiredArgsConstructor
public class RestImitationClient {

    @Qualifier("captureRedirectRestTemplate")
    private final RestTemplate restTemplate;
    @Setter
    private ImitationCookie cookie;


    public <T> ResponseEntity<T> get(URI uri, @Nullable Object body, Class<T> responseType) {
        HttpEntity<?> request = body != null
            ? new HttpEntity<>(body, cookie.jsonHeaders())
            : new HttpEntity<>(cookie.jsonHeaders());

        ResponseEntity<T> response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            request,
            responseType
        );
        cookie.addNewCookieValues(response);
        return response;
    }

    public <T> ResponseEntity<T> get(URI uri, Class<T> responseType) {
        return get(uri, null, responseType);
    }

    public <T> ResponseEntity<T> post(URI uri, @Nullable Object body, Class<T> responseType) {
        HttpEntity<?> request = body != null
            ? new HttpEntity<>(body, cookie.jsonHeaders())
            : new HttpEntity<>(cookie.jsonHeaders());

        ResponseEntity<T> response = restTemplate.exchange(
            uri,
            HttpMethod.POST,
            request,
            responseType
        );
        cookie.addNewCookieValues(response);
        return response;
    }

    public <T> ResponseEntity<T> post(URI uri, Class<T> responseType) {
        return post(uri, null, responseType);
    }

    public <T> ResponseEntity<T> exchange(URI uri, HttpMethod httpMethod, HttpEntity<?> request, Class<T> responseType) {
        return restTemplate.exchange(
            uri,
            httpMethod,
            request,
            responseType
        );
    }
}
