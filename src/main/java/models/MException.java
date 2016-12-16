package models;

public class MException extends RuntimeException {
    private static final long serialVersionUID = -4530140103715612384L;
    private String message;
    private MErrorCode errorCode = MErrorCode.GENERIC;
    public MException() {
        super();
        
    }
    public MException(String message) {
        super(message);
        this.message = message;
    }
    
    public MException(String message, MErrorCode errorCode) {
        super();
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
