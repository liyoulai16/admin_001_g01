package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.AboutStat;
import com.community.mapper.AboutStatMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AboutStatService {
    
    @Resource
    private AboutStatMapper aboutStatMapper;
    
    public List<AboutStat> getAllStats() {
        return aboutStatMapper.selectList(
            new LambdaQueryWrapper<AboutStat>()
                .eq(AboutStat::getStatus, 1)
                .eq(AboutStat::getDeleted, 0)
                .orderByAsc(AboutStat::getSortOrder)
        );
    }
    
    public AboutStat getStatById(Long id) {
        return aboutStatMapper.selectById(id);
    }
    
    public Page<AboutStat> getStatPage(int pageNum, int pageSize) {
        Page<AboutStat> page = new Page<>(pageNum, pageSize);
        return aboutStatMapper.selectPage(page,
            new LambdaQueryWrapper<AboutStat>()
                .eq(AboutStat::getDeleted, 0)
                .orderByAsc(AboutStat::getSortOrder)
        );
    }
    
    public boolean createStat(AboutStat stat) {
        if (!StringUtils.hasText(stat.getNumberText())) {
            return false;
        }
        if (!StringUtils.hasText(stat.getLabel())) {
            return false;
        }
        if (stat.getSortOrder() == null) {
            stat.setSortOrder(0);
        }
        if (stat.getStatus() == null) {
            stat.setStatus(1);
        }
        stat.setDeleted(0);
        stat.setCreateTime(LocalDateTime.now());
        stat.setUpdateTime(LocalDateTime.now());
        return aboutStatMapper.insert(stat) > 0;
    }
    
    public boolean updateStat(AboutStat stat) {
        AboutStat existing = aboutStatMapper.selectById(stat.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (StringUtils.hasText(stat.getNumberText())) {
            existing.setNumberText(stat.getNumberText());
        }
        if (StringUtils.hasText(stat.getLabel())) {
            existing.setLabel(stat.getLabel());
        }
        if (stat.getSortOrder() != null) {
            existing.setSortOrder(stat.getSortOrder());
        }
        if (stat.getStatus() != null) {
            existing.setStatus(stat.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return aboutStatMapper.updateById(existing) > 0;
    }
    
    public boolean deleteStat(Long id) {
        AboutStat existing = aboutStatMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return aboutStatMapper.updateById(existing) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        AboutStat existing = aboutStatMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return aboutStatMapper.updateById(existing) > 0;
    }
}
