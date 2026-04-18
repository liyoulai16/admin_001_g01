package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("service_category")
public class ServiceCategory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String icon;
    
    private Integer sortOrder;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private Integer serviceCount;
}
