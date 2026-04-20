package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.PageResult;
import com.community.entity.ForumPost;
import com.community.entity.User;
import com.community.mapper.ForumPostMapper;
import com.community.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ForumPostService {
    
    @Resource
    private ForumPostMapper postMapper;
    
    @Resource
    private UserMapper userMapper;
    
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public PageResult<ForumPost> getPostList(int page, int size, String sortBy, Long categoryId, String keyword) {
        if (size <= 0) size = DEFAULT_PAGE_SIZE;
        int offset = (page - 1) * size;
        
        List<ForumPost> posts;
        long total;
        
        if (StringUtils.hasText(keyword)) {
            posts = postMapper.selectPostListByKeyword(keyword, offset, size);
            total = postMapper.selectPostCountByKeyword(keyword);
        } else if (categoryId != null && categoryId > 0) {
            posts = postMapper.selectPostListByCategory(categoryId, offset, size);
            total = postMapper.selectPostCountByCategory(categoryId);
        } else {
            posts = postMapper.selectPostListWithDetails(offset, size);
            total = postMapper.selectPostCount();
        }
        
        for (ForumPost post : posts) {
            enrichPost(post);
        }
        
        sortPosts(posts, sortBy);
        
        return PageResult.of(posts, total, page, size);
    }
    
    private void sortPosts(List<ForumPost> posts, String sortBy) {
        if (posts == null || posts.isEmpty()) return;
        
        switch (sortBy) {
            case "hot":
                posts.sort((a, b) -> {
                    int scoreA = (a.getViews() != null ? a.getViews() : 0) + 
                                 (a.getComments() != null ? a.getComments() : 0) * 10 + 
                                 (a.getLikes() != null ? a.getLikes() : 0) * 5;
                    int scoreB = (b.getViews() != null ? b.getViews() : 0) + 
                                 (b.getComments() != null ? b.getComments() : 0) * 10 + 
                                 (b.getLikes() != null ? b.getLikes() : 0) * 5;
                    return Integer.compare(scoreB, scoreA);
                });
                break;
            case "essence":
                posts.removeIf(p -> p.getIsEssence() == null || p.getIsEssence() != 1);
                break;
            case "latest":
            default:
                posts.sort((a, b) -> {
                    if (a.getCreateTime() == null) return 1;
                    if (b.getCreateTime() == null) return -1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
                });
                break;
        }
    }
    
    private void enrichPost(ForumPost post) {
        if (post == null) return;
        
        post.setAuthorName(StringUtils.hasText(post.getAuthorName()) ? post.getAuthorName() : "匿名用户");
        post.setAuthorAvatar("👤");
        
        if (post.getCreateTime() != null) {
            post.setExcerpt(StringUtils.hasText(post.getExcerpt()) ? post.getExcerpt() : 
                (StringUtils.hasText(post.getContent()) && post.getContent().length() > 200 ? 
                    post.getContent().substring(0, 200) + "..." : post.getContent()));
        }
        
        if (StringUtils.hasText(post.getImages())) {
            try {
                List<String> imageList = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
                post.setImageList(imageList);
            } catch (Exception e) {
                post.setImageList(new ArrayList<>());
            }
        } else {
            post.setImageList(new ArrayList<>());
        }
        
        if (post.getViews() == null) post.setViews(0);
        if (post.getComments() == null) post.setComments(0);
        if (post.getLikes() == null) post.setLikes(0);
        if (post.getIsEssence() == null) post.setIsEssence(0);
        if (post.getIsHot() == null) post.setIsHot(0);
    }
    
    public ForumPost getPostById(Long id) {
        ForumPost post = postMapper.selectPostByIdWithDetails(id);
        if (post != null) {
            post.setViews((post.getViews() != null ? post.getViews() : 0) + 1);
            postMapper.updateById(post);
            enrichPost(post);
        }
        return post;
    }
    
    public Map<String, Object> createPost(Long userId, Long categoryId, String title, String content) {
        Map<String, Object> result = new HashMap<>();
        
        if (!StringUtils.hasText(title) || title.length() < 10) {
            result.put("success", false);
            result.put("message", "标题至少10个字符");
            return result;
        }
        
        if (!StringUtils.hasText(content)) {
            result.put("success", false);
            result.put("message", "请输入内容");
            return result;
        }
        
        if (categoryId == null || categoryId <= 0) {
            result.put("success", false);
            result.put("message", "请选择板块");
            return result;
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        
        ForumPost post = new ForumPost();
        post.setUserId(userId);
        post.setCategoryId(categoryId);
        post.setTitle(title);
        post.setContent(content);
        post.setExcerpt(content.length() > 200 ? content.substring(0, 200) + "..." : content);
        post.setViews(0);
        post.setComments(0);
        post.setLikes(0);
        post.setIsEssence(0);
        post.setIsHot(0);
        post.setStatus(1);
        post.setDeleted(0);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        
        postMapper.insert(post);
        
        result.put("success", true);
        result.put("message", "发布成功");
        result.put("id", post.getId());
        return result;
    }
    
    public List<Map<String, Object>> getActiveUsers(int limit) {
        List<Map<String, Object>> users = postMapper.selectActiveUsers(limit);
        for (Map<String, Object> user : users) {
            user.put("avatar", "👤");
            Object postsObj = user.get("posts");
            if (postsObj == null) {
                user.put("posts", 0);
            }
        }
        return users;
    }
    
    public Map<String, Object> likePost(Long postId) {
        Map<String, Object> result = new HashMap<>();
        
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            result.put("success", false);
            result.put("message", "帖子不存在");
            return result;
        }
        
        post.setLikes((post.getLikes() != null ? post.getLikes() : 0) + 1);
        postMapper.updateById(post);
        
        result.put("success", true);
        result.put("message", "点赞成功");
        result.put("likes", post.getLikes());
        return result;
    }
}
