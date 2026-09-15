package gr.grodov.grsso.attachment.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class FileStorageNotFoundException extends BaseErrorFieldException {
    public FileStorageNotFoundException() {
        super("file_storage_not_found");
    }
}
