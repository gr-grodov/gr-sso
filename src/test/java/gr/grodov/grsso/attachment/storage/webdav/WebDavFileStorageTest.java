package gr.grodov.grsso.attachment.storage.webdav;

import com.github.sardine.Sardine;
import gr.grodov.grsso.attachment.domain.entity.StorageType;
import gr.grodov.grsso.attachment.exception.FileNotFoundException;
import gr.grodov.grsso.attachment.exception.FileStorageDeleteException;
import gr.grodov.grsso.attachment.exception.FileStorageLoadException;
import gr.grodov.grsso.attachment.exception.FileStorageSaveException;
import gr.grodov.grsso.attachment.props.StorageAppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebDavFileStorageTest {

    private static final String BASE_URL = "http://localhost:8081";
    private static final String KEY = UUID.randomUUID().toString();
    private static final String FILE_URL = BASE_URL + "/" + KEY;

    @Mock
    private Sardine sardine;
    private WebDavFileStorage fileStorage;

    @BeforeEach
    void setUp() {
        StorageAppProperties.WebDavStorageProperties webDavProperties = Mockito.mock();
        StorageAppProperties properties = Mockito.mock();
        when(properties.webdav()).thenReturn(webDavProperties);
        when(webDavProperties.baseUrl()).thenReturn(BASE_URL);

        this.fileStorage = new WebDavFileStorage(properties, sardine);
    }

    @Test
    void type_returnWebDav() {
        var result = fileStorage.type();

        assertThat(result).isEqualTo(StorageType.WEB_DAV);
    }

    @Test
    void save_withCorrectData_saveFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "filename",
            "filename.jpg",
            "image/png",
            "file".getBytes()
        );

        fileStorage.save(KEY, file);

        verify(sardine).put(eq(FILE_URL), any(InputStream.class), eq("image/png"));
    }

    @Test
    void save_failPutWebDav_throwsFileStorageSaveException() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "filename",
            "filename.jpg",
            "image/png",
            "file".getBytes()
        );
        doThrow(IOException.class).when(sardine).put(eq(FILE_URL), any(InputStream.class), eq("image/png"));

        assertThatThrownBy(() -> fileStorage.save(KEY, file))
            .isExactlyInstanceOf(FileStorageSaveException.class)
            .satisfies(ex -> {
                var exception = (FileStorageSaveException) ex;
                assertThat(exception.getCode()).isEqualTo("failed_save_file");
            });
    }

    @Test
    void load_withExistFile_returnResource() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("file".getBytes());
        when(sardine.exists(FILE_URL)).thenReturn(true);
        when(sardine.get(FILE_URL)).thenReturn(inputStream);

        Resource result = fileStorage.load(KEY);

        assertThat(result).isNotNull();
        assertThat(result.getInputStream().readAllBytes()).isEqualTo("file".getBytes());
    }

    @Test
    void load_noExistFile_throwsFileNotFoundException() throws IOException {
        when(sardine.exists(FILE_URL)).thenReturn(false);

        assertThatThrownBy(() -> fileStorage.load(KEY))
            .isExactlyInstanceOf(FileNotFoundException.class)
            .satisfies(ex -> {
                var exception = (FileNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("file_not_found");
            });
    }

    @Test
    void load_failGetWebDav_throwsFileStorageLoadException() throws IOException {
        doThrow(IOException.class).when(sardine).exists(FILE_URL);

        assertThatThrownBy(() -> fileStorage.load(KEY))
            .isExactlyInstanceOf(FileStorageLoadException.class)
            .satisfies(ex -> {
                var exception = (FileStorageLoadException) ex;
                assertThat(exception.getCode()).isEqualTo("failed_load_file");
            });
    }

    @Test
    void delete_withExistFile_deleteFromWebDav() throws IOException {
        when(sardine.exists(FILE_URL)).thenReturn(true);

        fileStorage.delete(KEY);

        verify(sardine).delete(FILE_URL);
    }

    @Test
    void delete_withNoExistFile_noCallDeleteWebDav() throws IOException {
        when(sardine.exists(FILE_URL)).thenReturn(false);

        fileStorage.delete(KEY);

        verify(sardine, never()).delete(anyString());
    }

    @Test
    void delete_failDeleteDelete_throwsFileStorageDeleteException() throws IOException {
        when(sardine.exists(FILE_URL)).thenReturn(true);
        doThrow(IOException.class).when(sardine).delete(FILE_URL);

        assertThatThrownBy(() -> fileStorage.delete(KEY))
            .isExactlyInstanceOf(FileStorageDeleteException.class)
            .satisfies(ex -> {
                var exception = (FileStorageDeleteException) ex;
                assertThat(exception.getCode()).isEqualTo("failed_delete_file");
            });
    }

}