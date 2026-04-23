package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.AboutTeam;
import com.community.mapper.AboutTeamMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AboutTeamService {
    
    @Resource
    private AboutTeamMapper aboutTeamMapper;
    
    public List<AboutTeam> getAllTeams() {
        return aboutTeamMapper.selectList(
            new LambdaQueryWrapper<AboutTeam>()
                .eq(AboutTeam::getStatus, 1)
                .eq(AboutTeam::getDeleted, 0)
                .orderByAsc(AboutTeam::getSortOrder)
        );
    }
    
    public AboutTeam getTeamById(Long id) {
        return aboutTeamMapper.selectById(id);
    }
    
    public Page<AboutTeam> getTeamPage(int pageNum, int pageSize) {
        Page<AboutTeam> page = new Page<>(pageNum, pageSize);
        return aboutTeamMapper.selectPage(page,
            new LambdaQueryWrapper<AboutTeam>()
                .eq(AboutTeam::getDeleted, 0)
                .orderByAsc(AboutTeam::getSortOrder)
        );
    }
    
    public boolean createTeam(AboutTeam team) {
        if (!StringUtils.hasText(team.getName())) {
            return false;
        }
        if (!StringUtils.hasText(team.getRole())) {
            return false;
        }
        if (team.getSortOrder() == null) {
            team.setSortOrder(0);
        }
        if (team.getStatus() == null) {
            team.setStatus(1);
        }
        team.setDeleted(0);
        team.setCreateTime(LocalDateTime.now());
        team.setUpdateTime(LocalDateTime.now());
        return aboutTeamMapper.insert(team) > 0;
    }
    
    public boolean updateTeam(AboutTeam team) {
        AboutTeam existing = aboutTeamMapper.selectById(team.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (team.getAvatar() != null) {
            existing.setAvatar(team.getAvatar());
        }
        if (StringUtils.hasText(team.getName())) {
            existing.setName(team.getName());
        }
        if (StringUtils.hasText(team.getRole())) {
            existing.setRole(team.getRole());
        }
        if (team.getBio() != null) {
            existing.setBio(team.getBio());
        }
        if (team.getSortOrder() != null) {
            existing.setSortOrder(team.getSortOrder());
        }
        if (team.getStatus() != null) {
            existing.setStatus(team.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return aboutTeamMapper.updateById(existing) > 0;
    }
    
    public boolean deleteTeam(Long id) {
        AboutTeam existing = aboutTeamMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return aboutTeamMapper.updateById(existing) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        AboutTeam existing = aboutTeamMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return aboutTeamMapper.updateById(existing) > 0;
    }
}
