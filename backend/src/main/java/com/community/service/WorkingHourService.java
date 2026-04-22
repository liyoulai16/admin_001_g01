package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.WorkingHour;
import com.community.mapper.WorkingHourMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkingHourService {
    
    @Resource
    private WorkingHourMapper workingHourMapper;
    
    public List<WorkingHour> getAllWorkingHours() {
        return workingHourMapper.selectList(
            new LambdaQueryWrapper<WorkingHour>()
                .eq(WorkingHour::getStatus, 1)
                .eq(WorkingHour::getDeleted, 0)
                .orderByAsc(WorkingHour::getSortOrder)
        );
    }
    
    public WorkingHour getWorkingHourById(Long id) {
        return workingHourMapper.selectById(id);
    }
    
    public Page<WorkingHour> getWorkingHourPage(int pageNum, int pageSize) {
        Page<WorkingHour> page = new Page<>(pageNum, pageSize);
        return workingHourMapper.selectPage(page,
            new LambdaQueryWrapper<WorkingHour>()
                .eq(WorkingHour::getDeleted, 0)
                .orderByAsc(WorkingHour::getSortOrder)
        );
    }
    
    public boolean createWorkingHour(WorkingHour workingHour) {
        if (!StringUtils.hasText(workingHour.getDay())) {
            return false;
        }
        if (!StringUtils.hasText(workingHour.getTime())) {
            return false;
        }
        if (workingHour.getSortOrder() == null) {
            workingHour.setSortOrder(0);
        }
        if (workingHour.getStatus() == null) {
            workingHour.setStatus(1);
        }
        workingHour.setDeleted(0);
        workingHour.setCreateTime(LocalDateTime.now());
        workingHour.setUpdateTime(LocalDateTime.now());
        return workingHourMapper.insert(workingHour) > 0;
    }
    
    public boolean updateWorkingHour(WorkingHour workingHour) {
        WorkingHour existing = workingHourMapper.selectById(workingHour.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (StringUtils.hasText(workingHour.getDay())) {
            existing.setDay(workingHour.getDay());
        }
        if (StringUtils.hasText(workingHour.getTime())) {
            existing.setTime(workingHour.getTime());
        }
        if (workingHour.getSortOrder() != null) {
            existing.setSortOrder(workingHour.getSortOrder());
        }
        if (workingHour.getStatus() != null) {
            existing.setStatus(workingHour.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return workingHourMapper.updateById(existing) > 0;
    }
    
    public boolean deleteWorkingHour(Long id) {
        WorkingHour existing = workingHourMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return workingHourMapper.updateById(existing) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        WorkingHour existing = workingHourMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return workingHourMapper.updateById(existing) > 0;
    }
}
