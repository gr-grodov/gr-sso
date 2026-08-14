package gr.grodov.grsso.domain.entities.oauth_consent;

import gr.grodov.grsso.domain.converter.StringSetConverter;
import gr.grodov.grsso.domain.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "oauth2_authorization_consent")
public class OAuthConsent extends BaseEntity {

    @EmbeddedId
    private OAuthConsentId id;

    @Convert(converter = StringSetConverter.class)
    @Column(name = "authorities", length = 1000, nullable = false)
    private Set<String> authorities;
}
