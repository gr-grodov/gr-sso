package gr.grodov.grsso.attachment.api;

import gr.grodov.grsso.attachment.api.dto.request.AttachmentUploadResponse;
import gr.grodov.grsso.attachment.sevice.AttachmentService;
import gr.grodov.grsso.attachment.sevice.dto.AttachmentResource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AttachmentUploadResponse upload(
        @RequestParam("file") MultipartFile file
    ) throws InterruptedException {
        Thread.sleep(1000L);
        UUID id = attachmentService.upload(file);
        return new AttachmentUploadResponse(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getAttachment(@PathVariable UUID id) {
        AttachmentResource attachment = attachmentService.load(id);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(attachment.contentType()))
            .contentLength(attachment.size())
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"%s\"".formatted(attachment.originalName())
            ).body(attachment.resource());
    }
}
