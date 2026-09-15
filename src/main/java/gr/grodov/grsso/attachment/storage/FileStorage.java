package gr.grodov.grsso.attachment.storage;

import gr.grodov.grsso.attachment.exception.FileNotFoundException;
import gr.grodov.grsso.attachment.exception.FileStorageDeleteException;
import gr.grodov.grsso.attachment.exception.FileStorageLoadException;
import gr.grodov.grsso.attachment.exception.FileStorageSaveException;
import org.springframework.core.io.Resource;
import gr.grodov.grsso.attachment.domain.entity.StorageType;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    StorageType type();
    void save(String key, MultipartFile file) throws FileStorageSaveException;
    Resource load(String key) throws FileNotFoundException, FileStorageLoadException;
    void delete(String key) throws FileStorageDeleteException;
}
