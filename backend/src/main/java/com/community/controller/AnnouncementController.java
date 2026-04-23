package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.Announcement;
import com.community.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {
    
    @Resource
    private AnnouncementService announcementService;
    
    @GetMapping
    public Result<List<Announcement>> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementService.getPublishedAnnouncements();
        return Result.success(announcements);
    }
    
    @GetMapping("/page")
    public Result<Page<Announcement>> getPublishedAnnouncementPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Announcement> page = announcementService.getPublishedAnnouncementPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getPublishedAnnouncementById(id);
        if (announcement == null) {
            return Result.error("公告不存在或已下架");
        }
        announcementService.incrementViewCount(id);
        return Result.success(announcement);
    }
}
