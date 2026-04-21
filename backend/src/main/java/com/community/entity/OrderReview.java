package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "order_review", autoResultMap = true)
public class OrderReview {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private String orderNo;
    
    private Long userId;
    
    private Long serviceId;
    
    private Integer rating;
    
    private String content;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;
    
    private Integer isAnonymous;
    
    private Integer isVisible;
    
    private String reply;
    
    private LocalDateTime replyTime;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
