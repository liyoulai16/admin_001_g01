package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.entity.ForumCategory;
import com.community.mapper.ForumCategoryMapper;
import com.community.mapper.ForumPostMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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
}
