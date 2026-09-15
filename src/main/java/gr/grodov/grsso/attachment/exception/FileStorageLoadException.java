package gr.grodov.grsso.attachment.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class FileStorageLoadException extends BaseErrorFieldException {
    public FileStorageLoadException() {
        super("failed_load_file");
    }
}
