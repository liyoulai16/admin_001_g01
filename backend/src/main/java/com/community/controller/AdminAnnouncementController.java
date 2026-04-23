package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.Announcement;
import com.community.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/announcements")
@CrossOrigin(origins = "*")
public class AdminAnnouncementController {
    
    @Resource
    private AnnouncementService announcementService;
    
    @GetMapping
    public Result<Page<Announcement>> getAnnouncementList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Announcement> page = announcementService.getAnnouncementPage(pageNum, pageSize, keyword);
        return Result.success(page);
    }
    
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        if (announcement == null || announcement.getDeleted() == 1) {
            return Result.error("公告不存在");
        }
        return Result.success(announcement);
    }
    
    @PostMapping
    public Result<Map<String, Object>> createAnnouncement(@RequestBody Announcement announcement) {
        boolean success = announcementService.createAnnouncement(announcement);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("id", announcement.getId());
            return Result.success("创建成功", result);
        } else {
            return Result.error("创建失败，请检查必填字段");
        }
    }
    
    @PutMapping("/{id}")
    public Result<String> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        boolean success = announcementService.updateAnnouncement(announcement);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.deleteAnnouncement(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/{id}/toggle-pin")
    public Result<String> togglePin(@PathVariable Long id) {
        boolean success = announcementService.togglePin(id);
        if (success) {
            return Result.success("操作成功");
        } else {
            return Result.error("操作失败");
        }
    }
    
    @PutMapping("/{id}/toggle-status")
    public Result<String> toggleStatus(@PathVariable Long id) {
        boolean success = announcementService.toggleStatus(id);
        if (success) {
            return Result.success("操作成功");
        } else {
            return Result.error("操作失败");
        }
    }
}
