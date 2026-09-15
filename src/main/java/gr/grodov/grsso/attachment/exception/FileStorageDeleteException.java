package gr.grodov.grsso.attachment.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class FileStorageDeleteException extends BaseErrorFieldException {
    public FileStorageDeleteException() {
        super("failed_delete_file");
    }
}
