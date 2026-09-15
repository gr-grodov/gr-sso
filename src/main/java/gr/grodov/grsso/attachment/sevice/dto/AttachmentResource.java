package gr.grodov.grsso.attachment.sevice.dto;

import org.springframework.core.io.Resource;

public record AttachmentResource(
    Resource resource,
    String originalName,
    String contentType,
    Long size
) {
}
