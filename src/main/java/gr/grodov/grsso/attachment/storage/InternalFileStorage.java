package gr.grodov.grsso.attachment.storage;

import gr.grodov.grsso.attachment.domain.entity.StorageType;
import gr.grodov.grsso.attachment.exception.FileNotFoundException;
import gr.grodov.grsso.attachment.exception.FileStorageDeleteException;
import gr.grodov.grsso.attachment.exception.FileStorageSaveException;
import gr.grodov.grsso.attachment.props.StorageAppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class InternalFileStorage implements FileStorage {
    private final Path root;

    public InternalFileStorage(StorageAppProperties properties) {
        this.root = Paths.get(properties.internal().pathToSave()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Error init InternalFileStorage", e);
        }
    }

    @Override
    public StorageType type() {
        return StorageType.INTERNAL;
    }

    @Override
    public void save(String key, MultipartFile file) throws FileStorageSaveException {
        Path target = resolve(key);

        try {
            Files.createDirectories(target.getParent());

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileStorageSaveException();
        }
    }

    @Override
    public Resource load(String key) throws FileNotFoundException {
        Path path = resolve(key);

        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }
        return new FileSystemResource(path);
    }

    @Override
    public void delete(String key) throws FileStorageDeleteException {
        Path path = resolve(key);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileStorageDeleteException();
        }
    }

    private Path resolve(String key) {
        Path path = root.resolve(key).normalize();

        if (!path.startsWith(root)) {
            throw new IllegalArgumentException("Invalid storage key: " + key);
        }
        return path;
    }
}
