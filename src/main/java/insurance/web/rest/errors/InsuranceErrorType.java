package insurance.web.rest.errors;

import org.springframework.http.HttpStatus;

public enum InsuranceErrorType {
    // Internal Error
    GENERAL_ERROR(8500, HttpStatus.INTERNAL_SERVER_ERROR, "insurance.general_error"),
    NOT_FOUND(8450, HttpStatus.NOT_FOUND, "insurance.not_found"),
    BAD_REQUEST(8400, HttpStatus.BAD_REQUEST, "insurance.bad_request"),
    ;

    InsuranceErrorType(int code, HttpStatus httpStatus, String messageKey) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }

    private final int code;
    private final HttpStatus httpStatus;
    private final String messageKey;

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
