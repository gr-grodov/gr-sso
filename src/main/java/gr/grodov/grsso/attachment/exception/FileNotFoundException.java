package gr.grodov.grsso.attachment.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class FileNotFoundException extends BaseErrorFieldException {
    public FileNotFoundException() {
        super("file_not_found");
    }
}
