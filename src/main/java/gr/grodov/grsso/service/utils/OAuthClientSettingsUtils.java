package gr.grodov.grsso.service.utils;

import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.Map;

public class OAuthClientSettingsUtils {
    public static Map<String, Object> getDefaultClientSettings() {
        return ClientSettings.builder().build().getSettings();
    }

    public static Map<String, Object> getDefaultTokenSettings() {
        return TokenSettings.builder().build().getSettings();
    }
}
