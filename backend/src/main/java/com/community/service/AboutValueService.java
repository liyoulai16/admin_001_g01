package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.AboutValue;
import com.community.mapper.AboutValueMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AboutValueService {
    
    @Resource
    private AboutValueMapper aboutValueMapper;
    
    public List<AboutValue> getAllValues() {
        return aboutValueMapper.selectList(
            new LambdaQueryWrapper<AboutValue>()
                .eq(AboutValue::getStatus, 1)
                .eq(AboutValue::getDeleted, 0)
                .orderByAsc(AboutValue::getSortOrder)
        );
    }
    
    public AboutValue getValueById(Long id) {
        return aboutValueMapper.selectById(id);
    }
    
    public Page<AboutValue> getValuePage(int pageNum, int pageSize) {
        Page<AboutValue> page = new Page<>(pageNum, pageSize);
        return aboutValueMapper.selectPage(page,
            new LambdaQueryWrapper<AboutValue>()
                .eq(AboutValue::getDeleted, 0)
                .orderByAsc(AboutValue::getSortOrder)
        );
    }
    
    public boolean createValue(AboutValue value) {
        if (!StringUtils.hasText(value.getTitle())) {
            return false;
        }
        if (!StringUtils.hasText(value.getDescription())) {
            return false;
        }
        if (value.getSortOrder() == null) {
            value.setSortOrder(0);
        }
        if (value.getStatus() == null) {
            value.setStatus(1);
        }
        value.setDeleted(0);
        value.setCreateTime(LocalDateTime.now());
        value.setUpdateTime(LocalDateTime.now());
        return aboutValueMapper.insert(value) > 0;
    }
    
    public boolean updateValue(AboutValue value) {
        AboutValue existing = aboutValueMapper.selectById(value.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (value.getIcon() != null) {
            existing.setIcon(value.getIcon());
        }
        if (StringUtils.hasText(value.getTitle())) {
            existing.setTitle(value.getTitle());
        }
        if (StringUtils.hasText(value.getDescription())) {
            existing.setDescription(value.getDescription());
        }
        if (value.getSortOrder() != null) {
            existing.setSortOrder(value.getSortOrder());
        }
        if (value.getStatus() != null) {
            existing.setStatus(value.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return aboutValueMapper.updateById(existing) > 0;
    }
    
    public boolean deleteValue(Long id) {
        AboutValue existing = aboutValueMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return aboutValueMapper.updateById(existing) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        AboutValue existing = aboutValueMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return aboutValueMapper.updateById(existing) > 0;
    }
}
