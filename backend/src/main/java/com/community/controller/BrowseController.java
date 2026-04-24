package com.community.controller;

import com.community.common.Result;
import com.community.service.BrowseRecordService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/browse")
@CrossOrigin(origins = "*")
public class BrowseController {
    
    @Resource
    private BrowseRecordService browseRecordService;
    
    @GetMapping("/status")
    public Result<Map<String, Object>> getBrowsingStatus(
            @RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        Map<String, Object> status = browseRecordService.getBrowsingStatus(userId);
        return Result.success(status);
    }
    
    @PostMapping("/update-time")
    public Result<Map<String, Object>> updateBrowsingTime(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int seconds) {
        if (userId == null) {
            userId = 1L;
        }
        Map<String, Object> result = browseRecordService.updateBrowsingTime(userId, seconds);
        return Result.success(result);
    }
    
    @PostMapping("/claim-reward")
    public Result<Map<String, Object>> claimBrowseReward(
            @RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        Map<String, Object> result = browseRecordService.claimBrowseReward(userId);
        
        if ((Boolean) result.get("success")) {
            return Result.success(result.get("message"), result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
}
