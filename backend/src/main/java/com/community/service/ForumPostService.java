package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.entity.ForumCategory;
import com.community.entity.ForumPost;
import com.community.entity.User;
import com.community.mapper.ForumCategoryMapper;
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
    
    @Resource
    private ForumCategoryMapper categoryMapper;
    
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
        
        posts.sort((a, b) -> {
            int tagA = a.getTag() != null ? a.getTag() : 0;
            int tagB = b.getTag() != null ? b.getTag() : 0;
            
            boolean isTopA = (tagA == 2 || tagA == 3);
            boolean isTopB = (tagB == 2 || tagB == 3);
            
            if (isTopA && !isTopB) return -1;
            if (!isTopA && isTopB) return 1;
            
            switch (sortBy) {
                case "hot":
                    int scoreA = (a.getViews() != null ? a.getViews() : 0) + 
                                 (a.getComments() != null ? a.getComments() : 0) * 10 + 
                                 (a.getLikes() != null ? a.getLikes() : 0) * 5;
                    int scoreB = (b.getViews() != null ? b.getViews() : 0) + 
                                 (b.getComments() != null ? b.getComments() : 0) * 10 + 
                                 (b.getLikes() != null ? b.getLikes() : 0) * 5;
                    return Integer.compare(scoreB, scoreA);
                case "essence":
                    int essenceA = (a.getIsEssence() != null && a.getIsEssence() == 1) ? 1 : 0;
                    int essenceB = (b.getIsEssence() != null && b.getIsEssence() == 1) ? 1 : 0;
                    if (essenceA != essenceB) {
                        return Integer.compare(essenceB, essenceA);
                    }
                    if (a.getCreateTime() == null) return 1;
                    if (b.getCreateTime() == null) return -1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
                case "latest":
                default:
                    if (a.getCreateTime() == null) return 1;
                    if (b.getCreateTime() == null) return -1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
            }
        });
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
        if (post.getTag() == null) post.setTag(0);
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
        post.setTag(0);
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
    
    // ==================== 管理端方法 ====================
    
    public Page<ForumPost> getPostPageForAdmin(int pageNum, int pageSize, String keyword, Long categoryId, Integer status) {
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ForumPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ForumPost::getDeleted, 0)
                   .orderByDesc(ForumPost::getCreateTime);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(ForumPost::getTitle, keyword)
                .or()
                .like(ForumPost::getContent, keyword)
            );
        }
        
        if (categoryId != null && categoryId > 0) {
            queryWrapper.eq(ForumPost::getCategoryId, categoryId);
        }
        
        if (status != null) {
            queryWrapper.eq(ForumPost::getStatus, status);
        }
        
        Page<ForumPost> resultPage = postMapper.selectPage(page, queryWrapper);
        fillPostDetails(resultPage.getRecords());
        
        return resultPage;
    }
    
    public ForumPost getPostByIdForAdmin(Long id) {
        ForumPost post = postMapper.selectById(id);
        if (post != null && post.getDeleted() != 1) {
            fillPostDetails(java.util.Collections.singletonList(post));
        }
        return post;
    }
    
    private void fillPostDetails(List<ForumPost> posts) {
        for (ForumPost post : posts) {
            if (post.getCategoryId() != null) {
                ForumCategory category = categoryMapper.selectById(post.getCategoryId());
                if (category != null) {
                    post.setCategoryName(category.getName());
                }
            }
            if (post.getUserId() != null) {
                User user = userMapper.selectById(post.getUserId());
                if (user != null) {
                    post.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                }
            }
            enrichPost(post);
        }
    }
    
    public Map<String, Object> createPostByAdmin(ForumPost post) {
        Map<String, Object> result = new HashMap<>();
        
        if (!StringUtils.hasText(post.getTitle())) {
            result.put("success", false);
            result.put("message", "请输入标题");
            return result;
        }
        
        if (!StringUtils.hasText(post.getContent())) {
            result.put("success", false);
            result.put("message", "请输入内容");
            return result;
        }
        
        if (post.getCategoryId() == null) {
            result.put("success", false);
            result.put("message", "请选择版块");
            return result;
        }
        
        ForumCategory category = categoryMapper.selectById(post.getCategoryId());
        if (category == null || category.getDeleted() == 1) {
            result.put("success", false);
            result.put("message", "版块不存在");
            return result;
        }
        
        post.setUserId(1L);
        post.setViews(0);
        post.setComments(0);
        post.setLikes(0);
        if (post.getIsEssence() == null) post.setIsEssence(0);
        if (post.getIsHot() == null) post.setIsHot(0);
        if (post.getTag() == null) post.setTag(0);
        if (post.getStatus() == null) post.setStatus(1);
        post.setDeleted(0);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        
        if (post.getExcerpt() == null && post.getContent() != null) {
            post.setExcerpt(post.getContent().length() > 200 ? post.getContent().substring(0, 200) + "..." : post.getContent());
        }
        
        postMapper.insert(post);
        
        result.put("success", true);
        result.put("message", "创建成功");
        result.put("id", post.getId());
        return result;
    }
    
    public boolean updatePostByAdmin(ForumPost post) {
        ForumPost existing = postMapper.selectById(post.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        
        if (StringUtils.hasText(post.getTitle())) {
            existing.setTitle(post.getTitle());
        }
        if (post.getContent() != null) {
            existing.setContent(post.getContent());
            if (post.getExcerpt() == null) {
                existing.setExcerpt(post.getContent().length() > 200 ? post.getContent().substring(0, 200) + "..." : post.getContent());
            }
        }
        if (post.getExcerpt() != null) {
            existing.setExcerpt(post.getExcerpt());
        }
        if (post.getCategoryId() != null) {
            ForumCategory category = categoryMapper.selectById(post.getCategoryId());
            if (category != null && category.getDeleted() == 0) {
                existing.setCategoryId(post.getCategoryId());
            }
        }
        if (post.getImages() != null) {
            existing.setImages(post.getImages());
        }
        if (post.getIsEssence() != null) {
            existing.setIsEssence(post.getIsEssence());
        }
        if (post.getIsHot() != null) {
            existing.setIsHot(post.getIsHot());
        }
        if (post.getTag() != null) {
            existing.setTag(post.getTag());
        }
        if (post.getStatus() != null) {
            existing.setStatus(post.getStatus());
        }
        
        existing.setUpdateTime(LocalDateTime.now());
        return postMapper.updateById(existing) > 0;
    }
    
    public boolean deletePostByAdmin(Long id) {
        ForumPost existing = postMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return postMapper.updateById(existing) > 0;
    }
    
    public boolean setPostTop(Long id, Boolean isTop) {
        ForumPost existing = postMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        int currentTag = existing.getTag() != null ? existing.getTag() : 0;
        if (isTop) {
            if (currentTag == 1) {
                existing.setTag(2);
            } else if (currentTag == 3) {
                existing.setTag(3);
            } else {
                existing.setTag(2);
            }
        } else {
            if (currentTag == 2) {
                existing.setTag(0);
            } else if (currentTag == 3) {
                existing.setTag(1);
            }
        }
        existing.setUpdateTime(LocalDateTime.now());
        return postMapper.updateById(existing) > 0;
    }
    
    public boolean setPostHot(Long id, Boolean isHot) {
        ForumPost existing = postMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setIsHot(isHot ? 1 : 0);
        existing.setUpdateTime(LocalDateTime.now());
        return postMapper.updateById(existing) > 0;
    }
    
    public boolean setPostEssence(Long id, Boolean isEssence) {
        ForumPost existing = postMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        int currentTag = existing.getTag() != null ? existing.getTag() : 0;
        if (isEssence) {
            if (currentTag == 0) {
                existing.setTag(1);
            } else if (currentTag == 2) {
                existing.setTag(3);
            }
        } else {
            if (currentTag == 1) {
                existing.setTag(0);
            } else if (currentTag == 3) {
                existing.setTag(2);
            }
        }
        existing.setIsEssence(isEssence ? 1 : 0);
        existing.setUpdateTime(LocalDateTime.now());
        return postMapper.updateById(existing) > 0;
    }
    
    public boolean updatePostStatus(Long id, Integer status) {
        ForumPost existing = postMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return postMapper.updateById(existing) > 0;
    }
}
