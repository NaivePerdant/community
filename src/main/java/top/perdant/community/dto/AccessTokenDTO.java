package top.perdant.community.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 可以使用一些JSON注解来更简化的返回给控制层JSON字符串
 * 直接使用驼峰与下划线对应
 * 此处可以优化
 */

@Builder
@Getter
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
