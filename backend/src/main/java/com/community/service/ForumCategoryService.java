package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.ForumCategory;
import com.community.entity.ForumPost;
import com.community.mapper.ForumCategoryMapper;
import com.community.mapper.ForumPostMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ForumCategoryService {
    
    @Resource
    private ForumCategoryMapper categoryMapper;
    
    @Resource
    private ForumPostMapper postMapper;
    
    public List<ForumCategory> getAllCategories() {
        List<ForumCategory> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<ForumCategory>()
                .eq(ForumCategory::getStatus, 1)
                .eq(ForumCategory::getDeleted, 0)
                .orderByAsc(ForumCategory::getSortOrder)
        );
        
        for (ForumCategory category : categories) {
            long count = postMapper.selectPostCountByCategory(category.getId());
            category.setPostCount((int) count);
        }
        
        return categories;
    }
    
    public ForumCategory getCategoryById(Long id) {
        return categoryMapper.selectOne(
            new LambdaQueryWrapper<ForumCategory>()
                .eq(ForumCategory::getId, id)
                .eq(ForumCategory::getDeleted, 0)
        );
    }
    
    public Map<String, Object> createCategory(ForumCategory category) {
        Map<String, Object> result = new HashMap<>();
        
        ForumCategory existingCategory = categoryMapper.selectOne(
            new LambdaQueryWrapper<ForumCategory>()
                .eq(ForumCategory::getName, category.getName())
                .eq(ForumCategory::getDeleted, 0)
        );
        
        if (existingCategory != null) {
            result.put("success", false);
            result.put("message", "分类名称已存在");
            return result;
        }
        
        category.setStatus(1);
        category.setDeleted(0);
        categoryMapper.insert(category);
        
        result.put("success", true);
        result.put("message", "创建成功");
        result.put("id", category.getId());
        return result;
    }
    
    // ==================== 管理端方法 ====================
    
    public Page<ForumCategory> getCategoryPageForAdmin(int pageNum, int pageSize, String keyword) {
        Page<ForumCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ForumCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ForumCategory::getDeleted, 0)
                   .orderByAsc(ForumCategory::getSortOrder);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(ForumCategory::getName, keyword);
        }
        
        Page<ForumCategory> resultPage = categoryMapper.selectPage(page, queryWrapper);
        
        for (ForumCategory category : resultPage.getRecords()) {
            long count = postMapper.selectCount(
                new LambdaQueryWrapper<ForumPost>()
                    .eq(ForumPost::getCategoryId, category.getId())
                    .eq(ForumPost::getDeleted, 0)
            );
            category.setPostCount((int) count);
        }
        
        return resultPage;
    }
    
    public List<ForumCategory> getAllCategoriesForAdmin() {
        List<ForumCategory> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<ForumCategory>()
                .eq(ForumCategory::getDeleted, 0)
                .orderByAsc(ForumCategory::getSortOrder)
        );
        
        for (ForumCategory category : categories) {
            long count = postMapper.selectCount(
                new LambdaQueryWrapper<ForumPost>()
                    .eq(ForumPost::getCategoryId, category.getId())
                    .eq(ForumPost::getDeleted, 0)
            );
            category.setPostCount((int) count);
        }
        
        return categories;
    }
    
    public ForumCategory getCategoryByIdForAdmin(Long id) {
        ForumCategory category = categoryMapper.selectById(id);
        if (category != null && category.getDeleted() != 1) {
            long count = postMapper.selectCount(
                new LambdaQueryWrapper<ForumPost>()
                    .eq(ForumPost::getCategoryId, category.getId())
                    .eq(ForumPost::getDeleted, 0)
            );
            category.setPostCount((int) count);
        }
        return category;
    }
    
    public Map<String, Object> createCategoryByAdmin(ForumCategory category) {
        Map<String, Object> result = new HashMap<>();
        
        if (!StringUtils.hasText(category.getName())) {
            result.put("success", false);
            result.put("message", "请输入版块名称");
            return result;
        }
        
        ForumCategory existingCategory = categoryMapper.selectOne(
            new LambdaQueryWrapper<ForumCategory>()
                .eq(ForumCategory::getName, category.getName())
                .eq(ForumCategory::getDeleted, 0)
        );
        
        if (existingCategory != null) {
            result.put("success", false);
            result.put("message", "版块名称已存在");
            return result;
        }
        
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        category.setDeleted(0);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        categoryMapper.insert(category);
        
        result.put("success", true);
        result.put("message", "创建成功");
        result.put("id", category.getId());
        return result;
    }
    
    public boolean updateCategoryByAdmin(ForumCategory category) {
        ForumCategory existing = categoryMapper.selectById(category.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        
        if (StringUtils.hasText(category.getName())) {
            ForumCategory sameName = categoryMapper.selectOne(
                new LambdaQueryWrapper<ForumCategory>()
                    .eq(ForumCategory::getName, category.getName())
                    .eq(ForumCategory::getDeleted, 0)
            );
            if (sameName != null && !sameName.getId().equals(category.getId())) {
                return false;
            }
            existing.setName(category.getName());
        }
        
        if (category.getIcon() != null) {
            existing.setIcon(category.getIcon());
        }
        if (category.getSortOrder() != null) {
            existing.setSortOrder(category.getSortOrder());
        }
        if (category.getStatus() != null) {
            existing.setStatus(category.getStatus());
        }
        
        existing.setUpdateTime(LocalDateTime.now());
        return categoryMapper.updateById(existing) > 0;
    }
    
    public Map<String, Object> deleteCategoryByAdmin(Long id) {
        Map<String, Object> result = new HashMap<>();
        
        ForumCategory existing = categoryMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            result.put("success", false);
            result.put("message", "版块不存在");
            return result;
        }
        
        Long postCount = postMapper.selectCount(
            new LambdaQueryWrapper<ForumPost>()
                .eq(ForumPost::getCategoryId, id)
                .eq(ForumPost::getDeleted, 0)
        );
        
        if (postCount > 0) {
            result.put("success", false);
            result.put("message", "该版块下还有 " + postCount + " 个帖子，无法删除");
            return result;
        }
        
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(existing);
        
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }
    
    public boolean updateCategoryStatus(Long id, Integer status) {
        ForumCategory existing = categoryMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return categoryMapper.updateById(existing) > 0;
    }
}
