package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.ServiceCategory;
import com.community.entity.ServiceItem;
import com.community.mapper.ServiceCategoryMapper;
import com.community.mapper.ServiceItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceCategoryService {
    
    @Resource
    private ServiceCategoryMapper serviceCategoryMapper;
    
    @Resource
    private ServiceItemMapper serviceItemMapper;
    
    public List<ServiceCategory> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryMapper.selectList(
            new LambdaQueryWrapper<ServiceCategory>()
                .eq(ServiceCategory::getStatus, 1)
                .eq(ServiceCategory::getDeleted, 0)
                .orderByAsc(ServiceCategory::getSortOrder)
        );
        
        for (ServiceCategory category : categories) {
            Long count = serviceItemMapper.selectCount(
                new LambdaQueryWrapper<ServiceItem>()
                    .eq(ServiceItem::getCategoryId, category.getId())
                    .eq(ServiceItem::getStatus, 1)
                    .eq(ServiceItem::getDeleted, 0)
            );
            category.setServiceCount(count.intValue());
        }
        
        return categories;
    }
    
    public ServiceCategory getCategoryById(Long id) {
        return serviceCategoryMapper.selectById(id);
    }
    
    public ServiceCategory getCategoryByName(String name) {
        return serviceCategoryMapper.selectOne(
            new LambdaQueryWrapper<ServiceCategory>()
                .eq(ServiceCategory::getName, name)
                .eq(ServiceCategory::getDeleted, 0)
        );
    }
    
    public Page<ServiceCategory> getCategoryPage(int pageNum, int pageSize, String keyword) {
        Page<ServiceCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceCategory::getDeleted, 0)
                   .orderByAsc(ServiceCategory::getSortOrder);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(ServiceCategory::getName, keyword);
        }
        
        Page<ServiceCategory> resultPage = serviceCategoryMapper.selectPage(page, queryWrapper);
        
        for (ServiceCategory category : resultPage.getRecords()) {
            Long count = serviceItemMapper.selectCount(
                new LambdaQueryWrapper<ServiceItem>()
                    .eq(ServiceItem::getCategoryId, category.getId())
                    .eq(ServiceItem::getDeleted, 0)
            );
            category.setServiceCount(count.intValue());
        }
        
        return resultPage;
    }
    
    public boolean createCategory(ServiceCategory category) {
        if (!StringUtils.hasText(category.getName())) {
            return false;
        }
        if (getCategoryByName(category.getName()) != null) {
            return false;
        }
        category.setStatus(1);
        category.setDeleted(0);
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        return serviceCategoryMapper.insert(category) > 0;
    }
    
    public boolean updateCategory(ServiceCategory category) {
        ServiceCategory existing = serviceCategoryMapper.selectById(category.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (StringUtils.hasText(category.getName())) {
            ServiceCategory sameName = getCategoryByName(category.getName());
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
        return serviceCategoryMapper.updateById(existing) > 0;
    }
    
    public boolean deleteCategory(Long id) {
        ServiceCategory existing = serviceCategoryMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        Long serviceCount = serviceItemMapper.selectCount(
            new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getCategoryId, id)
                .eq(ServiceItem::getDeleted, 0)
        );
        if (serviceCount > 0) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return serviceCategoryMapper.updateById(existing) > 0;
    }
}
