package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("platform_intro")
public class PlatformIntro {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String content;
    
    private String imageUrl;
    
    private String stat1Label;
    
    private String stat1Value;
    
    private String stat2Label;
    
    private String stat2Value;
    
    private String stat3Label;
    
    private String stat3Value;
    
    private String stat4Label;
    
    private String stat4Value;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
