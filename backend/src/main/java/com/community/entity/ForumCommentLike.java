package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("forum_comment_like")
public class ForumCommentLike {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long commentId;
    
    private Long userId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
