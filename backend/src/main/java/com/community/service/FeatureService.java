package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.Feature;
import com.community.mapper.FeatureMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeatureService {
    
    @Resource
    private FeatureMapper featureMapper;
    
    public List<Feature> getAllFeatures() {
        return featureMapper.selectList(
            new LambdaQueryWrapper<Feature>()
                .eq(Feature::getStatus, 1)
                .eq(Feature::getDeleted, 0)
                .orderByAsc(Feature::getSortOrder)
        );
    }
    
    public Feature getFeatureById(Long id) {
        return featureMapper.selectById(id);
    }
    
    public Page<Feature> getFeaturePage(int pageNum, int pageSize) {
        Page<Feature> page = new Page<>(pageNum, pageSize);
        return featureMapper.selectPage(page,
            new LambdaQueryWrapper<Feature>()
                .eq(Feature::getDeleted, 0)
                .orderByAsc(Feature::getSortOrder)
        );
    }
    
    public boolean createFeature(Feature feature) {
        if (!StringUtils.hasText(feature.getTitle())) {
            return false;
        }
        if (!StringUtils.hasText(feature.getDescription())) {
            return false;
        }
        if (feature.getSortOrder() == null) {
            feature.setSortOrder(0);
        }
        if (feature.getStatus() == null) {
            feature.setStatus(1);
        }
        feature.setDeleted(0);
        feature.setCreateTime(LocalDateTime.now());
        feature.setUpdateTime(LocalDateTime.now());
        return featureMapper.insert(feature) > 0;
    }
    
    public boolean updateFeature(Feature feature) {
        Feature existing = featureMapper.selectById(feature.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (feature.getIcon() != null) {
            existing.setIcon(feature.getIcon());
        }
        if (StringUtils.hasText(feature.getTitle())) {
            existing.setTitle(feature.getTitle());
        }
        if (StringUtils.hasText(feature.getDescription())) {
            existing.setDescription(feature.getDescription());
        }
        if (feature.getSortOrder() != null) {
            existing.setSortOrder(feature.getSortOrder());
        }
        if (feature.getStatus() != null) {
            existing.setStatus(feature.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return featureMapper.updateById(existing) > 0;
    }
    
    public boolean deleteFeature(Long id) {
        Feature existing = featureMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return featureMapper.updateById(existing) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        Feature existing = featureMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return featureMapper.updateById(existing) > 0;
    }
}
