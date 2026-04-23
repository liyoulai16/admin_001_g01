package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.AboutMission;
import com.community.mapper.AboutMissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AboutMissionService {
    
    @Resource
    private AboutMissionMapper aboutMissionMapper;
    
    public AboutMission getMission() {
        List<AboutMission> list = aboutMissionMapper.selectList(
            new LambdaQueryWrapper<AboutMission>()
                .eq(AboutMission::getStatus, 1)
                .eq(AboutMission::getDeleted, 0)
                .orderByAsc(AboutMission::getId)
                .last("LIMIT 1")
        );
        return list.isEmpty() ? null : list.get(0);
    }
    
    public AboutMission getMissionById(Long id) {
        return aboutMissionMapper.selectById(id);
    }
    
    public Page<AboutMission> getMissionPage(int pageNum, int pageSize) {
        Page<AboutMission> page = new Page<>(pageNum, pageSize);
        return aboutMissionMapper.selectPage(page,
            new LambdaQueryWrapper<AboutMission>()
                .eq(AboutMission::getDeleted, 0)
                .orderByAsc(AboutMission::getId)
        );
    }
    
    public boolean createMission(AboutMission mission) {
        if (!StringUtils.hasText(mission.getTitle())) {
            return false;
        }
        if (mission.getStatus() == null) {
            mission.setStatus(1);
        }
        mission.setDeleted(0);
        mission.setCreateTime(LocalDateTime.now());
        mission.setUpdateTime(LocalDateTime.now());
        return aboutMissionMapper.insert(mission) > 0;
    }
    
    public boolean updateMission(AboutMission mission) {
        AboutMission existing = aboutMissionMapper.selectById(mission.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (StringUtils.hasText(mission.getTitle())) {
            existing.setTitle(mission.getTitle());
        }
        if (mission.getDescription() != null) {
            existing.setDescription(mission.getDescription());
        }
        if (mission.getImageUrl() != null) {
            existing.setImageUrl(mission.getImageUrl());
        }
        if (mission.getImageText() != null) {
            existing.setImageText(mission.getImageText());
        }
        if (mission.getStatus() != null) {
            existing.setStatus(mission.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return aboutMissionMapper.updateById(existing) > 0;
    }
    
    public boolean deleteMission(Long id) {
        AboutMission existing = aboutMissionMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return aboutMissionMapper.updateById(existing) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        AboutMission existing = aboutMissionMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return aboutMissionMapper.updateById(existing) > 0;
    }
}
