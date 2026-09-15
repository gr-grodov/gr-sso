package gr.grodov.grsso.attachment.sevice;

import gr.grodov.grsso.attachment.domain.entity.Attachment;
import gr.grodov.grsso.attachment.domain.entity.AttachmentStatus;
import gr.grodov.grsso.attachment.domain.entity.StorageType;
import gr.grodov.grsso.attachment.domain.repo.AttachmentRepo;
import gr.grodov.grsso.attachment.exception.AttachmentNotFoundException;
import gr.grodov.grsso.attachment.props.StorageAppProperties;
import gr.grodov.grsso.attachment.storage.FileStorage;
import gr.grodov.grsso.attachment.storage.FileStorageResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    private final static UUID ATTACHMENT_ID = UUID.randomUUID();

    @Mock
    private FileStorage fileStorage;

    @Mock
    private StorageAppProperties storageProperties;
    @Mock
    private AttachmentRepo attachmentRepo;
    @Mock
    private FileStorageResolver fileStorageResolver;
    @InjectMocks
    private AttachmentService attachmentService;

    @Test
    void upload_withCorrectData_saveAttachment() {
        Attachment attachment = Attachment.builder().id(UUID.randomUUID()).build();
        when(fileStorageResolver.getFileStorage(StorageType.INTERNAL)).thenReturn(fileStorage);
        when(attachmentRepo.save(any())).thenReturn(attachment);

        var result = attachmentService.upload(mockMultipartFile(), StorageType.INTERNAL);

        ArgumentCaptor<Attachment> savedAttachment = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentRepo).save(savedAttachment.capture());
        verify(fileStorage).save(any(), any());
        assertThat(savedAttachment.getValue()).isNotNull().satisfies(value -> {
            assertThat(value.getStorageType()).isEqualTo(StorageType.INTERNAL);
            assertThat(value.getStatus()).isEqualTo(AttachmentStatus.TEMPORARY);
            assertThat(value.getOriginalName()).isEqualTo("filename.jpg");
            assertThat(value.getContentType()).isEqualTo("image/png");
            assertThat(value.getSize()).isEqualTo(100_000L);
        });
    }

    @Test
    void load_withCorrectData_returnAttachmentResource() {
        Attachment attachment = Attachment.builder()
            .storageType(StorageType.INTERNAL)
            .originalName("filename.jpg")
            .contentType("image/png")
            .size(100_000L)
        .build();
        when(attachmentRepo.findById(ATTACHMENT_ID)).thenReturn(Optional.of(attachment));
        when(fileStorageResolver.getFileStorage(StorageType.INTERNAL)).thenReturn(fileStorage);

        var result = attachmentService.load(ATTACHMENT_ID);

        assertThat(result.originalName()).isEqualTo("filename.jpg");
        assertThat(result.contentType()).isEqualTo("image/png");
        assertThat(result.size()).isEqualTo(100_000L);
    }

    @Test
    void load_withNoExistAttachment_returnAttachmentResource() {
        when(attachmentRepo.findById(ATTACHMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.load(ATTACHMENT_ID))
            .isExactlyInstanceOf(AttachmentNotFoundException.class)
            .satisfies(ex -> {
                var exception = (AttachmentNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("attachment_not_found");
            });
    }

    @Test
    void deleteTemporaryAttachments_withCorrectAttachments_deleteFilesFromStorage() {
        List<Attachment> attachments = List.of(
            Attachment.builder().storageType(StorageType.INTERNAL).storageKey("1").build(),
            Attachment.builder().storageType(StorageType.INTERNAL).storageKey("2").build()
        );
        when(fileStorageResolver.getFileStorage(StorageType.INTERNAL)).thenReturn(fileStorage);
        when(attachmentRepo.findTemporaryAttachmentsForDelete(any())).thenReturn(attachments);

        attachmentService.deleteTemporaryAttachments();

        ArgumentCaptor<List<Attachment>> deleteAttachments = ArgumentCaptor.forClass(List.class);
        verify(attachmentRepo).deleteAll(deleteAttachments.capture());
        assertThat(deleteAttachments.getValue()).isNotNull()
            .map(Attachment::getStorageKey)
            .isNotEmpty()
            .hasSize(2)
            .contains("1")
            .contains("2");
    }

    @Test
    void deleteTemporaryAttachments_withInCorrectAttachment_skipFailedAttachment() {
        List<Attachment> attachments = List.of(
            Attachment.builder().storageType(StorageType.INTERNAL).storageKey("1").build(),
            Attachment.builder().storageType(StorageType.INTERNAL).storageKey("2").build()
        );
        when(fileStorageResolver.getFileStorage(StorageType.INTERNAL)).thenReturn(fileStorage);
        when(attachmentRepo.findTemporaryAttachmentsForDelete(any())).thenReturn(attachments);
        doThrow(RuntimeException.class).when(fileStorage).delete("1");

        attachmentService.deleteTemporaryAttachments();

        ArgumentCaptor<List<Attachment>> deleteAttachments = ArgumentCaptor.forClass(List.class);
        verify(attachmentRepo).deleteAll(deleteAttachments.capture());
        assertThat(deleteAttachments.getValue()).isNotNull()
            .map(Attachment::getStorageKey)
            .isNotEmpty()
            .hasSize(1)
            .contains("2");
    }

    @Test
    void attachAttachment_withCorrectId_changeStatus() {
        Attachment attachment = Attachment.builder()
            .id(ATTACHMENT_ID)
            .status(AttachmentStatus.TEMPORARY)
            .build();
        when(attachmentRepo.findById(ATTACHMENT_ID)).thenReturn(Optional.of(attachment));

        attachmentService.attachAttachment(ATTACHMENT_ID);

        ArgumentCaptor<Attachment> attachedAttachment = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentRepo).save(attachedAttachment.capture());
        assertThat(attachedAttachment.getValue()).isNotNull().satisfies(value -> {
            assertThat(value.getId()).isEqualTo(ATTACHMENT_ID);
            assertThat(value.getStatus()).isEqualTo(AttachmentStatus.ATTACHED);
        });
    }

    @Test
    void attachAttachment_withEmptyId_noCallRepo() {
        attachmentService.attachAttachment(null);

        verifyNoInteractions(attachmentRepo);
    }

    @Test
    void attachAttachment_withEmptyAttachment_throwsAttachmentNotFoundException() {
        when(attachmentRepo.findById(ATTACHMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.attachAttachment(ATTACHMENT_ID))
            .isExactlyInstanceOf(AttachmentNotFoundException.class)
            .satisfies(ex -> {
                var exception = (AttachmentNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("attachment_not_found");
            });
    }

    @Test
    void attachAttachment_withAlreadyAttached_noCallSave() {
        Attachment attachment = Attachment.builder()
            .id(ATTACHMENT_ID)
            .status(AttachmentStatus.ATTACHED)
            .build();
        when(attachmentRepo.findById(ATTACHMENT_ID)).thenReturn(Optional.of(attachment));

        attachmentService.attachAttachment(ATTACHMENT_ID);

        verify(attachmentRepo, never()).save(any());
    }

    @Test
    void detachAttachment_withCorrectId_changeStatus() {
        Attachment attachment = Attachment.builder()
            .id(ATTACHMENT_ID)
            .status(AttachmentStatus.ATTACHED)
            .build();
        when(attachmentRepo.findById(ATTACHMENT_ID)).thenReturn(Optional.of(attachment));

        attachmentService.detachAttachment(ATTACHMENT_ID);
        ArgumentCaptor<Attachment> attachedAttachment = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentRepo).save(attachedAttachment.capture());
        assertThat(attachedAttachment.getValue()).isNotNull().satisfies(value -> {
            assertThat(value.getId()).isEqualTo(ATTACHMENT_ID);
            assertThat(value.getStatus()).isEqualTo(AttachmentStatus.TEMPORARY);
        });
    }

    @Test
    void detachAttachment_withEmptyId_noCallRepo() {
        attachmentService.detachAttachment(null);

        verifyNoInteractions(attachmentRepo);
    }

    @Test
    void detachAttachment_withEmptyAttachment_withoutSave() {
        when(attachmentRepo.findById(ATTACHMENT_ID)).thenReturn(Optional.empty());

        attachmentService.detachAttachment(ATTACHMENT_ID);

        verify(attachmentRepo, never()).save(any());
    }

    @Test
    void detachAttachment_withAlreadyTemporaryAttachment_withoutSave() {
        Attachment attachment = Attachment.builder()
            .id(ATTACHMENT_ID)
            .status(AttachmentStatus.TEMPORARY)
            .build();
        when(attachmentRepo.findById(ATTACHMENT_ID)).thenReturn(Optional.of(attachment));

        attachmentService.detachAttachment(ATTACHMENT_ID);

        verify(attachmentRepo, never()).save(any());
    }

    private MultipartFile mockMultipartFile() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("filename.jpg");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(100_000L);
        return file;
    }
}