package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("forum_post")
public class ForumPost {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long categoryId;
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private String excerpt;
    
    private String images;
    
    @TableField(exist = false)
    private List<String> imageList;
    
    private Integer views;
    
    private Integer comments;
    
    private Integer likes;
    
    private Integer isEssence;
    
    private Integer isHot;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String categoryName;
    
    @TableField(exist = false)
    private String authorName;
    
    @TableField(exist = false)
    private String authorAvatar;
}
