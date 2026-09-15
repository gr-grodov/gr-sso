package gr.grodov.grsso.attachment.job;

import gr.grodov.grsso.attachment.sevice.AttachmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TemporaryAttachmentDeleteJobTest {

    @Mock
    private AttachmentService attachmentService;
    @InjectMocks
    private TemporaryAttachmentDeleteJob deleteJob;

    @Test
    void delete_callDeleteTemporaryAttachments() {
        deleteJob.delete();

        verify(attachmentService).deleteTemporaryAttachments();
    }
}