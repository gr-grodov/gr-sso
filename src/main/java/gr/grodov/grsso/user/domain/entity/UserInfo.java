package gr.grodov.grsso.user.domain.entity;

import gr.grodov.grsso.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid default uuidv7()", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    private String email;

    private String password;

    @NotNull
    private Boolean enabled;

    @Column(name = "external_id")
    private String externalId;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private AuthProvider provider;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private String patronymic;
}
