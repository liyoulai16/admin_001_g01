package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contact_message")
public class ContactMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String phone;
    
    private String email;
    
    private String type;
    
    private String message;
    
    private Integer status;
    
    private String reply;
    
    private Long replyBy;
    
    private LocalDateTime replyTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
