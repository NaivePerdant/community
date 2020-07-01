package top.perdant.community.exception;

public class CustomizeException extends RuntimeException {
    private Integer code;
    private String message;
    public CustomizeException(CustomizeErrorCode errorCode){
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
