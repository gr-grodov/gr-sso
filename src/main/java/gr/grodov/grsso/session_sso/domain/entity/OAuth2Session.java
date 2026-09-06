package gr.grodov.grsso.session_sso.domain.entity;

import gr.grodov.grsso.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "oauth2_session")
public class OAuth2Session extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID DEFAULT uuidv7()")
    private UUID sid;

    @Column(name = "authorization_id", nullable = false)
    private String authorizationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Length(max = 100)
    @Column(name = "device_ip_address", nullable = false)
    private String deviceIpAddress;

    @Nullable
    @Column(name = "device_location_country")
    String deviceLocationCountry;

    @Nullable
    @Column(name = "device_location_city")
    String deviceLocationCity;

    @Length(max = 255)
    @Column(name = "device_user_agent")
    private String deviceUserAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;
}
