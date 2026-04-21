package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.ForumCategory;
import com.community.entity.ForumPost;
import com.community.service.ForumCategoryService;
import com.community.service.ForumPostService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminForumController {
    
    @Resource
    private ForumPostService forumPostService;
    
    @Resource
    private ForumCategoryService forumCategoryService;
    
    // ==================== 帖子管理 ====================
    
    @GetMapping("/forum-posts")
    public Result<Page<ForumPost>> getPostList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        Page<ForumPost> page = forumPostService.getPostPageForAdmin(pageNum, pageSize, keyword, categoryId, status);
        return Result.success(page);
    }
    
    @GetMapping("/forum-posts/{id}")
    public Result<ForumPost> getPostDetail(@PathVariable Long id) {
        ForumPost post = forumPostService.getPostByIdForAdmin(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        return Result.success(post);
    }
    
    @PostMapping("/forum-posts")
    public Result<Map<String, Object>> createPost(@RequestBody ForumPost post) {
        Map<String, Object> result = forumPostService.createPostByAdmin(post);
        if ((Boolean) result.get("success")) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", result.get("id"));
            return Result.success("创建成功", data);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @PutMapping("/forum-posts/{id}")
    public Result<String> updatePost(@PathVariable Long id, @RequestBody ForumPost post) {
        post.setId(id);
        boolean success = forumPostService.updatePostByAdmin(post);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/forum-posts/{id}")
    public Result<String> deletePost(@PathVariable Long id) {
        boolean success = forumPostService.deletePostByAdmin(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/forum-posts/{id}/top")
    public Result<String> setTop(@PathVariable Long id, @RequestParam Boolean isTop) {
        boolean success = forumPostService.setPostTop(id, isTop);
        if (success) {
            return Result.success(isTop ? "已置顶" : "已取消置顶");
        } else {
            return Result.error("操作失败");
        }
    }
    
    @PutMapping("/forum-posts/{id}/hot")
    public Result<String> setHot(@PathVariable Long id, @RequestParam Boolean isHot) {
        boolean success = forumPostService.setPostHot(id, isHot);
        if (success) {
            return Result.success(isHot ? "已设为热门" : "已取消热门");
        } else {
            return Result.error("操作失败");
        }
    }
    
    @PutMapping("/forum-posts/{id}/essence")
    public Result<String> setEssence(@PathVariable Long id, @RequestParam Boolean isEssence) {
        boolean success = forumPostService.setPostEssence(id, isEssence);
        if (success) {
            return Result.success(isEssence ? "已设为精华" : "已取消精华");
        } else {
            return Result.error("操作失败");
        }
    }
    
    @PutMapping("/forum-posts/{id}/status")
    public Result<String> updatePostStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = forumPostService.updatePostStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
    
    // ==================== 版块管理 ====================
    
    @GetMapping("/forum-categories")
    public Result<Page<ForumCategory>> getCategoryList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<ForumCategory> page = forumCategoryService.getCategoryPageForAdmin(pageNum, pageSize, keyword);
        return Result.success(page);
    }
    
    @GetMapping("/forum-categories/all")
    public Result<java.util.List<ForumCategory>> getAllCategories() {
        java.util.List<ForumCategory> categories = forumCategoryService.getAllCategoriesForAdmin();
        return Result.success(categories);
    }
    
    @GetMapping("/forum-categories/{id}")
    public Result<ForumCategory> getCategoryDetail(@PathVariable Long id) {
        ForumCategory category = forumCategoryService.getCategoryByIdForAdmin(id);
        if (category == null) {
            return Result.error("版块不存在");
        }
        return Result.success(category);
    }
    
    @PostMapping("/forum-categories")
    public Result<Map<String, Object>> createCategory(@RequestBody ForumCategory category) {
        Map<String, Object> result = forumCategoryService.createCategoryByAdmin(category);
        if ((Boolean) result.get("success")) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", result.get("id"));
            return Result.success("创建成功", data);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @PutMapping("/forum-categories/{id}")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody ForumCategory category) {
        category.setId(id);
        boolean success = forumCategoryService.updateCategoryByAdmin(category);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败，分类名称可能已存在");
        }
    }
    
    @DeleteMapping("/forum-categories/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        Map<String, Object> result = forumCategoryService.deleteCategoryByAdmin(id);
        if ((Boolean) result.get("success")) {
            return Result.success("删除成功");
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @PutMapping("/forum-categories/{id}/status")
    public Result<String> updateCategoryStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = forumCategoryService.updateCategoryStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
}
