package gr.grodov.grsso.attachment.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class AttachmentNotFoundException extends BaseErrorFieldException {
    public AttachmentNotFoundException() {
        super("attachment_not_found");
    }
}
