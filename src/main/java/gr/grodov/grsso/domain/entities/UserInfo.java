package gr.grodov.grsso.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "user_seq",
    sequenceName = "users_id_seq"
)
@Table(name = "users")
public class UserInfo extends BaseEntity {
    @NotNull
    String email;

    String password;

    @NotNull
    Boolean enabled;

    @Column(name = "external_id")
    String externalId;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    AuthProvider provider;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    Role role;
}
