package gr.grodov.grsso.attachment.storage;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import gr.grodov.grsso.attachment.domain.entity.StorageType;
import gr.grodov.grsso.attachment.exception.FileNotFoundException;
import gr.grodov.grsso.attachment.exception.FileStorageDeleteException;
import gr.grodov.grsso.attachment.exception.FileStorageLoadException;
import gr.grodov.grsso.attachment.exception.FileStorageSaveException;
import gr.grodov.grsso.attachment.props.StorageAppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class WebDavFileStorage implements FileStorage {
    private final Sardine sardine;
    private final String baseUrl;

    public WebDavFileStorage(StorageAppProperties properties) {
        this.baseUrl = properties.webdav().baseUrl().replaceAll("/$", "");
        this.sardine = SardineFactory.begin(properties.webdav().username(), properties.webdav().password());
    }

    @Override
    public StorageType type() {
        return StorageType.WEB_DAVE;
    }

    @Override
    public void save(String key, MultipartFile file) throws FileStorageSaveException {
        String url = buildUrl(key);

        try {
            sardine.put(url, file.getInputStream(), file.getContentType());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileStorageSaveException();
        }
    }

    @Override
    public Resource load(String key) throws FileNotFoundException, FileStorageLoadException {
        String url = buildUrl(key);

        try {
            if (!sardine.exists(url)) {
                throw new FileNotFoundException();
            }

            InputStream inputStream = sardine.get(url);
            return new InputStreamResource(inputStream);
        } catch (IOException e) {
            throw new FileStorageLoadException();
        }
    }

    @Override
    public void delete(String key) throws FileStorageDeleteException {
        String url = buildUrl(key);

        try {
            if (sardine.exists(url)) {
                sardine.delete(url);
            }
        } catch (IOException e) {
            throw new FileStorageDeleteException();
        }
    }

    private String buildUrl(String key) {
        return baseUrl + "/" + key;
    }
}
