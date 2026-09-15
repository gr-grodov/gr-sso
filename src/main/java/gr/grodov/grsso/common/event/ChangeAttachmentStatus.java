package gr.grodov.grsso.common.event;

import java.util.UUID;

public record ChangeAttachmentStatus(
    UUID attachmentId,
    Boolean attach
) { }
