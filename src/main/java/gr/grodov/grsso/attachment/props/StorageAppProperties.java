package gr.grodov.grsso.attachment.props;

import gr.grodov.grsso.attachment.domain.entity.StorageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@Valid
@ConfigurationProperties(prefix = "grsso.storage")
public record StorageAppProperties(
    @NotNull StorageType defaultType,
    @NotNull Long delayDeleteTemporaryAttachment,
    InternalStorageProperties internal,
    WebDavStorageProperties webdav
) {
    public record InternalStorageProperties(
        String pathToSave
    ) {}

    public record WebDavStorageProperties(
        String baseUrl,
        String username,
        String password
    ) {}
}
