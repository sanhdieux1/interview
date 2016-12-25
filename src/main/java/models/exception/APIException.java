package models.exception;

public class APIException extends Exception {
    private static final long serialVersionUID = -4530140103715612384L;
    private String message;
    private MErrorCode errorCode = MErrorCode.GENERIC;
    public APIException() {
        super();
        
    }
    public APIException(String message) {
        super(message);
        this.message = message;
    }
    public APIException(String message, Throwable t) {
        super(message, t);
        this.message = message;
    }
    public APIException(String message, MErrorCode errorCode) {
        super();
        this.message = message;
        this.errorCode = errorCode;
    }
    public APIException(String message, MErrorCode errorCode, Throwable t) {
        super(t);
        this.message = message;
        this.errorCode = errorCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public MErrorCode getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(MErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    
}
