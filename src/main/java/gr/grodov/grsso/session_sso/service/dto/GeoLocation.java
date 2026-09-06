package gr.grodov.grsso.session_sso.service.dto;

import org.springframework.modulith.NamedInterface;

@NamedInterface("service")
public record GeoLocation(
    String country,
    String city
) {
    public static GeoLocation unknown() {
        return new GeoLocation("Unknown", "Unknown");
    }
}