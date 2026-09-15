package gr.grodov.grsso.attachment.sevice;

import gr.grodov.grsso.attachment.domain.entity.Attachment;
import gr.grodov.grsso.attachment.domain.entity.AttachmentStatus;
import gr.grodov.grsso.attachment.domain.entity.StorageType;
import gr.grodov.grsso.attachment.domain.repo.AttachmentRepo;
import gr.grodov.grsso.attachment.exception.AttachmentNotFoundException;
import gr.grodov.grsso.attachment.props.StorageAppProperties;
import gr.grodov.grsso.attachment.sevice.dto.AttachmentResource;
import gr.grodov.grsso.attachment.storage.FileStorage;
import gr.grodov.grsso.attachment.storage.FileStorageResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final StorageAppProperties storageProperties;
    private final AttachmentRepo attachmentRepo;
    private final FileStorageResolver fileStorageResolver;

    @Transactional
    public UUID upload(MultipartFile file) {
        return upload(file, storageProperties.defaultType());
    }

    @Transactional
    public UUID upload(MultipartFile file, StorageType type) {
        FileStorage fileStorage = fileStorageResolver.getFileStorage(type);
        String storageKey = UUID.randomUUID().toString();

        fileStorage.save(storageKey, file);

        Attachment attachment = Attachment.builder()
            .originalName(file.getOriginalFilename())
            .contentType(file.getContentType())
            .size(file.getSize())
            .storageKey(storageKey)
            .storageType(type)
            .status(AttachmentStatus.TEMPORARY)
        .build();
        return attachmentRepo.save(attachment).getId();
    }

    @Transactional(readOnly = true)
    public AttachmentResource load(UUID id) {
        Attachment attachment = attachmentRepo.findById(id).orElseThrow(AttachmentNotFoundException::new);
        FileStorage fileStorage = fileStorageResolver.getFileStorage(attachment.getStorageType());

        return new AttachmentResource(
            fileStorage.load(attachment.getStorageKey()),
            attachment.getOriginalName(),
            attachment.getContentType(),
            attachment.getSize()
        );
    }

    @Transactional
    public void deleteTemporaryAttachments() {
        Instant before = Instant.now().minus(Duration.ofMinutes(storageProperties.delayDeleteTemporaryAttachment()));
        List<Attachment> temporaryAttachments = attachmentRepo.findTemporaryAttachmentsForDelete(before);
        FileStorage fileStorage;

        List<Attachment> attachmentsForDelete = new ArrayList<>();
        for (Attachment attachment: temporaryAttachments) {
            try {
                fileStorage = fileStorageResolver.getFileStorage(attachment.getStorageType());
                fileStorage.delete(attachment.getStorageKey());

                attachmentsForDelete.add(attachment);
            } catch (Exception exception) {
                log.error("Error delete attachment {}", attachment.getId(), exception);
            }
        }

        attachmentRepo.deleteAll(attachmentsForDelete);
    }
}
