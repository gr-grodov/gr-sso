package gr.grodov.grsso.attachment.domain.repo;

import gr.grodov.grsso.attachment.domain.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepo extends JpaRepository<Attachment, UUID> {
    @Query("""
        SELECT a FROM Attachment a
        WHERE a.status = 'TEMPORARY' AND a.createdAt < :before
    """)
    List<Attachment> findTemporaryAttachmentsForDelete(Instant before);
}