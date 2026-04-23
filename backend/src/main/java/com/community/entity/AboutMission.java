package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("about_mission")
public class AboutMission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String description;
    
    private String imageUrl;
    
    private String imageText;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
