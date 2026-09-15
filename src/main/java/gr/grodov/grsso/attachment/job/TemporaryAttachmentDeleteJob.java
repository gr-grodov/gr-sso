package gr.grodov.grsso.attachment.job;

import gr.grodov.grsso.attachment.sevice.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TemporaryAttachmentDeleteJob {

    private final AttachmentService attachmentService;

    @Scheduled(fixedDelayString = "${grsso.storage.delay-delete-temporary-attachment}", timeUnit = TimeUnit.MINUTES)
    public void delete() {
        attachmentService.deleteTemporaryAttachments();
    }
}
