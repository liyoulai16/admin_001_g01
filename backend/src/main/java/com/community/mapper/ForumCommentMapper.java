package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ForumCommentMapper extends BaseMapper<ForumComment> {
    
    @Select("SELECT fc.*, su.nickname as user_name, rtu.nickname as reply_to_user_name " +
            "FROM forum_comment fc " +
            "LEFT JOIN sys_user su ON fc.user_id = su.id " +
            "LEFT JOIN sys_user rtu ON fc.reply_to_user_id = rtu.id " +
            "WHERE fc.post_id = #{postId} AND fc.parent_id = 0 AND fc.deleted = 0 AND fc.status = 1 " +
            "ORDER BY fc.create_time ASC")
    List<ForumComment> selectRootCommentsByPostId(@Param("postId") Long postId);
    
    @Select("SELECT fc.*, su.nickname as user_name, rtu.nickname as reply_to_user_name " +
            "FROM forum_comment fc " +
            "LEFT JOIN sys_user su ON fc.user_id = su.id " +
            "LEFT JOIN sys_user rtu ON fc.reply_to_user_id = rtu.id " +
            "WHERE fc.parent_id = #{parentId} AND fc.deleted = 0 AND fc.status = 1 " +
            "ORDER BY fc.create_time ASC")
    List<ForumComment> selectRepliesByParentId(@Param("parentId") Long parentId);
    
    @Select("SELECT COUNT(*) FROM forum_comment WHERE post_id = #{postId} AND deleted = 0 AND status = 1")
    long countByPostId(@Param("postId") Long postId);
}
