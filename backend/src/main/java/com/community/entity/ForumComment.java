package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("forum_comment")
public class ForumComment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long postId;
    
    private Long userId;
    
    private Long parentId;
    
    private Long replyToId;
    
    private Long replyToUserId;
    
    private String content;
    
    private Integer likes;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private String userName;
    
    @TableField(exist = false)
    private String userAvatar;
    
    @TableField(exist = false)
    private String replyToUserName;
    
    @TableField(exist = false)
    private List<ForumComment> replies;
    
    @TableField(exist = false)
    private Boolean isLiked;
}
