package com.community.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.context.LoginUserContext;
import com.community.entity.ForumCategory;
import com.community.entity.ForumPost;
import com.community.entity.User;
import com.community.service.ForumCategoryService;
import com.community.service.ForumPostService;
import com.community.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {
    
    @Resource
    private ForumCategoryService categoryService;
    
    @Resource
    private ForumPostService postService;
    
    @Resource
    private UserService userService;
    
    @GetMapping("/categories")
    public Result<List<ForumCategory>> getCategories() {
        List<ForumCategory> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
    
    @GetMapping("/posts")
    public Result<PageResult<ForumPost>> getPosts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "latest") String sortBy,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        PageResult<ForumPost> result = postService.getPostList(page, size, sortBy, categoryId, keyword);
        return Result.success(result);
    }
    
    @GetMapping("/posts/{id}")
    public Result<ForumPost> getPostById(@PathVariable("id") Long id) {
        ForumPost post = postService.getPostById(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        return Result.success(post);
    }
    
    @PostMapping("/posts")
    public Result<Map<String, Object>> createPost(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("title") String title,
            @RequestParam("content") String content) {
        
        String currentUsername = LoginUserContext.getCurrentUser();
        if (currentUsername == null) {
            return Result.error(401, "请先登录");
        }
        
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        
        Map<String, Object> result = postService.createPost(user.getId(), categoryId, title, content);
        
        if ((Boolean) result.get("success")) {
            return Result.success((String) result.get("message"), result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @GetMapping("/active-users")
    public Result<List<Map<String, Object>>> getActiveUsers(
            @RequestParam(value = "limit", required = false, defaultValue = "5") int limit) {
        List<Map<String, Object>> users = postService.getActiveUsers(limit);
        return Result.success(users);
    }
    
    @PostMapping("/posts/{id}/like")
    public Result<Map<String, Object>> likePost(@PathVariable("id") Long id) {
        Map<String, Object> result = postService.likePost(id);
        
        if ((Boolean) result.get("success")) {
            return Result.success((String) result.get("message"), result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @GetMapping("/search")
    public Result<Map<String, Object>> searchPosts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "sortBy", required = false, defaultValue = "latest") String sortBy,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        
        PageResult<ForumPost> postPage = postService.getPostList(page, size, sortBy, categoryId, keyword);
        List<ForumCategory> categories = categoryService.getAllCategories();
        List<Map<String, Object>> activeUsers = postService.getActiveUsers(5);
        
        Map<String, Object> result = new HashMap<>();
        result.put("posts", postPage.getRecords());
        result.put("categories", categories);
        result.put("activeUsers", activeUsers);
        result.put("total", postPage.getTotal());
        result.put("current", postPage.getCurrent());
        result.put("size", postPage.getSize());
        result.put("pages", postPage.getPages());
        
        return Result.success(result);
    }
}
