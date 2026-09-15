package gr.grodov.grsso.attachment.storage;

import gr.grodov.grsso.attachment.domain.entity.StorageType;
import gr.grodov.grsso.attachment.exception.FileStorageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileStorageResolver {
    private final Map<StorageType, FileStorage> storages;

    @Autowired
    public FileStorageResolver(List<FileStorage> storages) {
        this.storages = storages.stream()
            .collect(Collectors.toUnmodifiableMap(
                FileStorage::type,
                Function.identity()
            ));
    }

    public FileStorage getFileStorage(StorageType storageType) throws FileStorageNotFoundException {
        FileStorage fileStorage = storages.get(storageType);
        if (fileStorage == null) {
            throw new FileStorageNotFoundException();
        }
        return fileStorage;
    }
}
