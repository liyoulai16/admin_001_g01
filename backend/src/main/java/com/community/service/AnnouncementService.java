package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.Announcement;
import com.community.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {
    
    @Resource
    private AnnouncementMapper announcementMapper;
    
    public List<Announcement> getPublishedAnnouncements() {
        return announcementMapper.selectList(
            new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, 1)
                .eq(Announcement::getDeleted, 0)
                .orderByDesc(Announcement::getIsPinned)
                .orderByAsc(Announcement::getSortOrder)
                .orderByDesc(Announcement::getCreateTime)
        );
    }
    
    public Page<Announcement> getPublishedAnnouncementPage(int pageNum, int pageSize) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        return announcementMapper.selectPage(page,
            new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, 1)
                .eq(Announcement::getDeleted, 0)
                .orderByDesc(Announcement::getIsPinned)
                .orderByAsc(Announcement::getSortOrder)
                .orderByDesc(Announcement::getCreateTime)
        );
    }
    
    public Announcement getAnnouncementById(Long id) {
        return announcementMapper.selectById(id);
    }
    
    public Announcement getPublishedAnnouncementById(Long id) {
        return announcementMapper.selectOne(
            new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getId, id)
                .eq(Announcement::getStatus, 1)
                .eq(Announcement::getDeleted, 0)
        );
    }
    
    public void incrementViewCount(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement != null) {
            announcement.setViewCount((announcement.getViewCount() == null ? 0 : announcement.getViewCount()) + 1);
            announcement.setUpdateTime(LocalDateTime.now());
            announcementMapper.updateById(announcement);
        }
    }
    
    public Page<Announcement> getAnnouncementPage(int pageNum, int pageSize, String keyword) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>()
            .eq(Announcement::getDeleted, 0)
            .orderByDesc(Announcement::getIsPinned)
            .orderByAsc(Announcement::getSortOrder)
            .orderByDesc(Announcement::getCreateTime);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(Announcement::getTitle, keyword)
                .or()
                .like(Announcement::getContent, keyword)
            );
        }
        
        return announcementMapper.selectPage(page, wrapper);
    }
    
    public boolean createAnnouncement(Announcement announcement) {
        if (!StringUtils.hasText(announcement.getTitle())) {
            return false;
        }
        if (!StringUtils.hasText(announcement.getContent())) {
            return false;
        }
        if (announcement.getIsPinned() == null) {
            announcement.setIsPinned(0);
        }
        if (announcement.getSortOrder() == null) {
            announcement.setSortOrder(0);
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus(0);
        }
        if (announcement.getViewCount() == null) {
            announcement.setViewCount(0);
        }
        announcement.setDeleted(0);
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        return announcementMapper.insert(announcement) > 0;
    }
    
    public boolean updateAnnouncement(Announcement announcement) {
        Announcement existing = announcementMapper.selectById(announcement.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (StringUtils.hasText(announcement.getTitle())) {
            existing.setTitle(announcement.getTitle());
        }
        if (announcement.getContent() != null) {
            existing.setContent(announcement.getContent());
        }
        if (announcement.getSummary() != null) {
            existing.setSummary(announcement.getSummary());
        }
        if (announcement.getCoverImage() != null) {
            existing.setCoverImage(announcement.getCoverImage());
        }
        if (announcement.getIsPinned() != null) {
            existing.setIsPinned(announcement.getIsPinned());
        }
        if (announcement.getSortOrder() != null) {
            existing.setSortOrder(announcement.getSortOrder());
        }
        if (announcement.getStatus() != null) {
            existing.setStatus(announcement.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return announcementMapper.updateById(existing) > 0;
    }
    
    public boolean deleteAnnouncement(Long id) {
        Announcement existing = announcementMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return announcementMapper.updateById(existing) > 0;
    }
    
    public boolean togglePin(Long id) {
        Announcement existing = announcementMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setIsPinned(existing.getIsPinned() == null || existing.getIsPinned() == 0 ? 1 : 0);
        existing.setUpdateTime(LocalDateTime.now());
        return announcementMapper.updateById(existing) > 0;
    }
    
    public boolean toggleStatus(Long id) {
        Announcement existing = announcementMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(existing.getStatus() == null || existing.getStatus() == 0 ? 1 : 0);
        existing.setUpdateTime(LocalDateTime.now());
        return announcementMapper.updateById(existing) > 0;
    }
}
