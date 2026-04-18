package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.entity.ServiceCategory;
import com.community.entity.ServiceItem;
import com.community.mapper.ServiceCategoryMapper;
import com.community.mapper.ServiceItemMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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
}
