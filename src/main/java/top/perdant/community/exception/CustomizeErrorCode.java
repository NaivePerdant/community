package top.perdant.community.exception;

/**
 * 感觉可以不需要枚举和接口！
 * 直接使用一个普通类，里面设置一些静态字段即可
 *
 * @author perdant
 */
public enum  CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND("找的问题不存在");
    private String message;

    CustomizeErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
