package gr.grodov.grsso.attachment.storage;

import gr.grodov.grsso.attachment.domain.entity.StorageType;
import gr.grodov.grsso.attachment.exception.FileStorageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageResolverTest {

    private FileStorageResolver storageResolver;

    @Mock
    private FileStorage fileStorage;

    @BeforeEach
    void setUp() {
        when(fileStorage.type()).thenReturn(StorageType.INTERNAL);
        this.storageResolver = new FileStorageResolver(List.of(fileStorage));
    }

    @Test
    void getFileStorage_withExistFileStorage_returnFileStorage() {
        var result = storageResolver.getFileStorage(StorageType.INTERNAL);

        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(StorageType.INTERNAL);
    }

    @Test
    void getFileStorage_withNoExistFileStorage_throwsFileStorageNotFoundException() {
        assertThatThrownBy(() -> storageResolver.getFileStorage(StorageType.WEB_DAV))
            .isExactlyInstanceOf(FileStorageNotFoundException.class)
            .satisfies(ex -> {
                var exception = (FileStorageNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("file_storage_not_found");
            });
    }

}