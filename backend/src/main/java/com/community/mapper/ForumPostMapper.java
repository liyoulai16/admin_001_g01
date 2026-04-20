package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {
    
    @Select("SELECT fp.*, fc.name as category_name, su.nickname as author_name " +
            "FROM forum_post fp " +
            "LEFT JOIN forum_category fc ON fp.category_id = fc.id " +
            "LEFT JOIN sys_user su ON fp.user_id = su.id " +
            "WHERE fp.deleted = 0 AND fp.status = 1 " +
            "ORDER BY fp.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<ForumPost> selectPostListWithDetails(@Param("offset") int offset, @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM forum_post WHERE deleted = 0 AND status = 1")
    long selectPostCount();
    
    @Select("SELECT fp.*, fc.name as category_name, su.nickname as author_name " +
            "FROM forum_post fp " +
            "LEFT JOIN forum_category fc ON fp.category_id = fc.id " +
            "LEFT JOIN sys_user su ON fp.user_id = su.id " +
            "WHERE fp.deleted = 0 AND fp.status = 1 AND fp.category_id = #{categoryId} " +
            "ORDER BY fp.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<ForumPost> selectPostListByCategory(@Param("categoryId") Long categoryId, 
                                               @Param("offset") int offset, 
                                               @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM forum_post WHERE deleted = 0 AND status = 1 AND category_id = #{categoryId}")
    long selectPostCountByCategory(@Param("categoryId") Long categoryId);
    
    @Select("SELECT fp.*, fc.name as category_name, su.nickname as author_name " +
            "FROM forum_post fp " +
            "LEFT JOIN forum_category fc ON fp.category_id = fc.id " +
            "LEFT JOIN sys_user su ON fp.user_id = su.id " +
            "WHERE fp.deleted = 0 AND fp.status = 1 " +
            "AND (fp.title LIKE CONCAT('%', #{keyword}, '%') OR fp.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY fp.create_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<ForumPost> selectPostListByKeyword(@Param("keyword") String keyword, 
                                               @Param("offset") int offset, 
                                               @Param("limit") int limit);
    
    @Select("SELECT COUNT(*) FROM forum_post WHERE deleted = 0 AND status = 1 " +
            "AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%'))")
    long selectPostCountByKeyword(@Param("keyword") String keyword);
    
    @Select("SELECT fp.*, fc.name as category_name, su.nickname as author_name " +
            "FROM forum_post fp " +
            "LEFT JOIN forum_category fc ON fp.category_id = fc.id " +
            "LEFT JOIN sys_user su ON fp.user_id = su.id " +
            "WHERE fp.deleted = 0 AND fp.status = 1 AND fp.id = #{id}")
    ForumPost selectPostByIdWithDetails(@Param("id") Long id);
    
    @Select("SELECT su.id, su.nickname as name, COUNT(fp.id) as posts " +
            "FROM sys_user su " +
            "LEFT JOIN forum_post fp ON su.id = fp.user_id AND fp.deleted = 0 AND fp.status = 1 " +
            "WHERE su.deleted = 0 AND su.status = 1 " +
            "GROUP BY su.id, su.nickname " +
            "ORDER BY posts DESC " +
            "LIMIT #{limit}")
    List<java.util.Map<String, Object>> selectActiveUsers(@Param("limit") int limit);
}
