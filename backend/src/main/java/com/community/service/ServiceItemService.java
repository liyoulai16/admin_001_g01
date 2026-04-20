package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.entity.ServiceCategory;
import com.community.entity.ServiceItem;
import com.community.mapper.ServiceCategoryMapper;
import com.community.mapper.ServiceItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ServiceItemService {
    
    @Resource
    private ServiceItemMapper serviceItemMapper;
    
    @Resource
    private ServiceCategoryMapper serviceCategoryMapper;
    
    public List<ServiceItem> getAllServices() {
        List<ServiceItem> services = serviceItemMapper.selectList(
            new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getStatus, 1)
                .eq(ServiceItem::getDeleted, 0)
                .orderByAsc(ServiceItem::getSortOrder)
        );
        
        fillCategoryName(services);
        return services;
    }
    
    public ServiceItem getServiceById(Long id) {
        ServiceItem service = serviceItemMapper.selectById(id);
        if (service != null && service.getCategoryId() != null) {
            ServiceCategory category = serviceCategoryMapper.selectById(service.getCategoryId());
            if (category != null) {
                service.setCategory(category.getName());
            }
        }
        return service;
    }
    
    public List<ServiceItem> getServicesByCategoryId(Long categoryId) {
        List<ServiceItem> services = serviceItemMapper.selectList(
            new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getCategoryId, categoryId)
                .eq(ServiceItem::getStatus, 1)
                .eq(ServiceItem::getDeleted, 0)
                .orderByAsc(ServiceItem::getSortOrder)
        );
        
        fillCategoryName(services);
        return services;
    }
    
    public List<ServiceItem> getServicesByCategoryName(String categoryName) {
        ServiceCategory category = serviceCategoryMapper.selectOne(
            new LambdaQueryWrapper<ServiceCategory>()
                .eq(ServiceCategory::getName, categoryName)
                .eq(ServiceCategory::getDeleted, 0)
        );
        
        if (category == null) {
            return Collections.emptyList();
        }
        
        return getServicesByCategoryId(category.getId());
    }
    
    public PageResult<ServiceItem> searchServices(String keyword, String categoryName, 
            String priceRange, BigDecimal minPrice, BigDecimal maxPrice,
            String sortBy, long current, long size) {
        LambdaQueryWrapper<ServiceItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceItem::getStatus, 1)
                   .eq(ServiceItem::getDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            String lowerKeyword = keyword.toLowerCase();
            queryWrapper.and(wrapper -> wrapper
                .like(ServiceItem::getName, lowerKeyword)
                .or()
                .like(ServiceItem::getDescription, lowerKeyword)
            );
        }
        
        if (StringUtils.hasText(categoryName) && !"全部".equals(categoryName)) {
            ServiceCategory category = serviceCategoryMapper.selectOne(
                new LambdaQueryWrapper<ServiceCategory>()
                    .eq(ServiceCategory::getName, categoryName)
                    .eq(ServiceCategory::getDeleted, 0)
            );
            if (category != null) {
                queryWrapper.eq(ServiceItem::getCategoryId, category.getId());
            }
        }
        
        if (minPrice != null && maxPrice != null) {
            queryWrapper.between(ServiceItem::getPriceValue, minPrice, maxPrice);
        } else if (minPrice != null) {
            queryWrapper.ge(ServiceItem::getPriceValue, minPrice);
        } else if (maxPrice != null) {
            queryWrapper.le(ServiceItem::getPriceValue, maxPrice);
        } else if (StringUtils.hasText(priceRange) && !"全部".equals(priceRange)) {
            switch (priceRange) {
                case "免费":
                    queryWrapper.and(wrapper -> wrapper
                        .eq(ServiceItem::getPriceValue, BigDecimal.ZERO)
                        .or()
                        .like(ServiceItem::getPrice, "免费")
                    );
                    break;
                case "低价":
                    queryWrapper.lt(ServiceItem::getPriceValue, new BigDecimal("50"))
                               .gt(ServiceItem::getPriceValue, BigDecimal.ZERO);
                    break;
                case "中价":
                    queryWrapper.between(ServiceItem::getPriceValue, new BigDecimal("50"), new BigDecimal("100"));
                    break;
                case "高价":
                    queryWrapper.gt(ServiceItem::getPriceValue, new BigDecimal("100"));
                    break;
            }
        }
        
        if (StringUtils.hasText(sortBy)) {
            switch (sortBy) {
                case "rating":
                    queryWrapper.orderByDesc(ServiceItem::getRating);
                    break;
                case "reviews":
                    queryWrapper.orderByDesc(ServiceItem::getReviews);
                    break;
                case "price-asc":
                    queryWrapper.orderByAsc(ServiceItem::getPriceValue);
                    break;
                case "price-desc":
                    queryWrapper.orderByDesc(ServiceItem::getPriceValue);
                    break;
                default:
                    queryWrapper.orderByAsc(ServiceItem::getSortOrder);
                    break;
            }
        } else {
            queryWrapper.orderByAsc(ServiceItem::getSortOrder);
        }
        
        Page<ServiceItem> page = new Page<>(current, size);
        Page<ServiceItem> resultPage = serviceItemMapper.selectPage(page, queryWrapper);
        fillCategoryName(resultPage.getRecords());
        
        return PageResult.of(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize()
        );
    }
    
    private void fillCategoryName(List<ServiceItem> services) {
        for (ServiceItem service : services) {
            if (service.getCategoryId() != null) {
                ServiceCategory category = serviceCategoryMapper.selectById(service.getCategoryId());
                if (category != null) {
                    service.setCategory(category.getName());
                }
            }
        }
    }
    
    public Page<ServiceItem> getServicePage(int pageNum, int pageSize, String keyword, Long categoryId) {
        Page<ServiceItem> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceItem::getDeleted, 0)
                   .orderByAsc(ServiceItem::getSortOrder);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(ServiceItem::getName, keyword);
        }
        
        if (categoryId != null && categoryId > 0) {
            queryWrapper.eq(ServiceItem::getCategoryId, categoryId);
        }
        
        Page<ServiceItem> resultPage = serviceItemMapper.selectPage(page, queryWrapper);
        fillCategoryName(resultPage.getRecords());
        
        return resultPage;
    }
    
    public boolean createService(ServiceItem service) {
        if (!StringUtils.hasText(service.getName())) {
            return false;
        }
        if (service.getCategoryId() == null) {
            return false;
        }
        ServiceCategory category = serviceCategoryMapper.selectById(service.getCategoryId());
        if (category == null || category.getDeleted() == 1) {
            return false;
        }
        service.setStatus(1);
        service.setDeleted(0);
        if (service.getSortOrder() == null) {
            service.setSortOrder(0);
        }
        if (service.getRating() == null) {
            service.setRating(BigDecimal.ZERO);
        }
        if (service.getReviews() == null) {
            service.setReviews(0);
        }
        service.setCreateTime(LocalDateTime.now());
        service.setUpdateTime(LocalDateTime.now());
        return serviceItemMapper.insert(service) > 0;
    }
    
    public boolean updateService(ServiceItem service) {
        ServiceItem existing = serviceItemMapper.selectById(service.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (StringUtils.hasText(service.getName())) {
            existing.setName(service.getName());
        }
        if (service.getCategoryId() != null) {
            ServiceCategory category = serviceCategoryMapper.selectById(service.getCategoryId());
            if (category != null && category.getDeleted() == 0) {
                existing.setCategoryId(service.getCategoryId());
            }
        }
        if (service.getIcon() != null) {
            existing.setIcon(service.getIcon());
        }
        if (service.getDescription() != null) {
            existing.setDescription(service.getDescription());
        }
        if (service.getPrice() != null) {
            existing.setPrice(service.getPrice());
        }
        if (service.getPriceValue() != null) {
            existing.setPriceValue(service.getPriceValue());
        }
        if (service.getRating() != null) {
            existing.setRating(service.getRating());
        }
        if (service.getReviews() != null) {
            existing.setReviews(service.getReviews());
        }
        if (service.getFeatures() != null) {
            existing.setFeatures(service.getFeatures());
        }
        if (service.getDetails() != null) {
            existing.setDetails(service.getDetails());
        }
        if (service.getSortOrder() != null) {
            existing.setSortOrder(service.getSortOrder());
        }
        if (service.getStatus() != null) {
            existing.setStatus(service.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return serviceItemMapper.updateById(existing) > 0;
    }
    
    public boolean deleteService(Long id) {
        ServiceItem existing = serviceItemMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return serviceItemMapper.updateById(existing) > 0;
    }
}
