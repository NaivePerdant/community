package top.perdant.community.exception;

/**
 * 感觉可以不需要枚举和接口！
 * 直接使用一个普通类，里面设置一些静态字段即可
 *
 * @author perdant
 */
public enum  CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(2001,"找的问题不存在"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题和评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登陆，请登陆后重试"),
    SYSTEM_ERROR(2004,"服务器端发生错误"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论不存在"),
    COMMENT_NOT_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008,"读取的回复不属于你！"),
    NOTIFICATION_NOT_FOUND(2009,"读取的回复不存在");
    private Integer code;
    private String message;

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    CustomizeErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
