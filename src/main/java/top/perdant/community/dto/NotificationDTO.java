package top.perdant.community.dto;

import lombok.Data;

@Data
public class NotificationDTO {

    private Long id;

    private Long notifier;

    private String notifierName;

    private String outerTitle;

    private Long outerId;

    private String type;

    private Integer status;

    private Long gmtCreate;

}
