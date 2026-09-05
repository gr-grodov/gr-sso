package gr.grodov.grsso.session_sso.domain.entity;

import gr.grodov.grsso.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

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

    @Column(name = "authorization_id")
    private String authorizationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "device_id")
    private String deviceId;

    @Length(max = 100)
    @Column(name = "device_ip_address")
    private String deviceIpAddress;

    @Length(max = 255)
    @Column(name = "device_user_agent")
    private String deviceUserAgent;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;
}
