package gr.grodov.grsso.authorization_sso.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OAuthConsentId implements Serializable {
    @Column(name = "registered_client_id", length = 100)
    private String registeredClientId;

    @Column(name = "principal_name", length = 200)
    private String principalName;
}
