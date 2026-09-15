package gr.grodov.grsso.attachment.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class FileStorageSaveException extends BaseErrorFieldException {
    public FileStorageSaveException() {
        super("failed_save_file");
    }
}
