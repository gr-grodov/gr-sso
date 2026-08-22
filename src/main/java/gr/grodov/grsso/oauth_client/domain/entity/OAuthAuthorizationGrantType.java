package gr.grodov.grsso.oauth_client.domain.entity;

import org.springframework.modulith.NamedInterface;

import java.util.Arrays;

@NamedInterface("domain")
public enum OAuthAuthorizationGrantType {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials"),
    JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer"),
    DEVICE_CODE("urn:ietf:params:oauth:grant-type:device_code"),
    TOKEN_EXCHANGE("urn:ietf:params:oauth:grant-type:token-exchange");

    private final String value;

    OAuthAuthorizationGrantType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OAuthAuthorizationGrantType getByValue(String value) {
        return Arrays.stream(values())
            .filter(method -> method.value.equals(value))
            .findAny()
            .orElse(null);
    }
}
