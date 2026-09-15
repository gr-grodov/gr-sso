package gr.grodov.grsso.attachment.domain.entity;

import gr.grodov.grsso.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "id default uuidv7()", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size")
    Long size;

    @Column(name = "storage_key")
    String storageKey;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "storage_type")
    StorageType storageType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    AttachmentStatus status;
}
