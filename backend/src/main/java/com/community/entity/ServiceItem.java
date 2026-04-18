package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "service_item", autoResultMap = true)
public class ServiceItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long categoryId;
    
    private String name;
    
    private String icon;
    
    private String description;
    
    private String price;
    
    private BigDecimal priceValue;
    
    private BigDecimal rating;
    
    private Integer reviews;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> features;
    
    private String details;
    
    private Integer sortOrder;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String category;
}
