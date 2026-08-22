package gr.grodov.grsso.authorization_sso.domain.entity;

import gr.grodov.grsso.common.domain.converter.StringSetConverter;
import gr.grodov.grsso.common.domain.BaseEntity;
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
