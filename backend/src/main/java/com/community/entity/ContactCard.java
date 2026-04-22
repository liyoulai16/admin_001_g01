package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("contact_card")
public class ContactCard {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String icon;
    
    private String title;
    
    private String detail;
    
    private String hint;
    
    private Integer sortOrder;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
