package gr.grodov.grsso.attachment.sevice.dto;

import org.springframework.core.io.Resource;
import org.springframework.modulith.NamedInterface;

@NamedInterface("service")
public record AttachmentResource(
    Resource resource,
    String originalName,
    String contentType,
    Long size
) {
}
