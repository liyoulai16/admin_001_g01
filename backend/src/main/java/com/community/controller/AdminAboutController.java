package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.AboutMission;
import com.community.entity.AboutStat;
import com.community.entity.AboutTeam;
import com.community.entity.AboutValue;
import com.community.service.AboutMissionService;
import com.community.service.AboutStatService;
import com.community.service.AboutTeamService;
import com.community.service.AboutValueService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminAboutController {
    
    @Resource
    private AboutMissionService aboutMissionService;
    
    @Resource
    private AboutValueService aboutValueService;
    
    @Resource
    private AboutStatService aboutStatService;
    
    @Resource
    private AboutTeamService aboutTeamService;
    
    // ==================== 我们的使命管理 ====================
    
    @GetMapping("/about-missions")
    public Result<Page<AboutMission>> getMissionList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<AboutMission> page = aboutMissionService.getMissionPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/about-missions/current")
    public Result<AboutMission> getCurrentMission() {
        AboutMission mission = aboutMissionService.getMission();
        if (mission == null) {
            return Result.error("暂无使命数据");
        }
        return Result.success(mission);
    }
    
    @GetMapping("/about-missions/{id}")
    public Result<AboutMission> getMissionDetail(@PathVariable Long id) {
        AboutMission item = aboutMissionService.getMissionById(id);
        if (item == null) {
            return Result.error("使命数据不存在");
        }
        return Result.success(item);
    }
    
    @PostMapping("/about-missions")
    public Result<Map<String, Object>> createMission(@RequestBody AboutMission item) {
        boolean success = aboutMissionService.createMission(item);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            return Result.success("创建成功", data);
        } else {
            return Result.error("创建失败，请填写必填字段");
        }
    }
    
    @PutMapping("/about-missions/{id}")
    public Result<String> updateMission(@PathVariable Long id, @RequestBody AboutMission item) {
        item.setId(id);
        boolean success = aboutMissionService.updateMission(item);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/about-missions/{id}")
    public Result<String> deleteMission(@PathVariable Long id) {
        boolean success = aboutMissionService.deleteMission(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/about-missions/{id}/status")
    public Result<String> updateMissionStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = aboutMissionService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
    
    // ==================== 我们的价值观管理 ====================
    
    @GetMapping("/about-values")
    public Result<Page<AboutValue>> getValueList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<AboutValue> page = aboutValueService.getValuePage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/about-values/all")
    public Result<List<AboutValue>> getAllValues() {
        List<AboutValue> list = aboutValueService.getAllValues();
        return Result.success(list);
    }
    
    @GetMapping("/about-values/{id}")
    public Result<AboutValue> getValueDetail(@PathVariable Long id) {
        AboutValue item = aboutValueService.getValueById(id);
        if (item == null) {
            return Result.error("价值观不存在");
        }
        return Result.success(item);
    }
    
    @PostMapping("/about-values")
    public Result<Map<String, Object>> createValue(@RequestBody AboutValue item) {
        boolean success = aboutValueService.createValue(item);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            return Result.success("创建成功", data);
        } else {
            return Result.error("创建失败，请填写必填字段");
        }
    }
    
    @PutMapping("/about-values/{id}")
    public Result<String> updateValue(@PathVariable Long id, @RequestBody AboutValue item) {
        item.setId(id);
        boolean success = aboutValueService.updateValue(item);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/about-values/{id}")
    public Result<String> deleteValue(@PathVariable Long id) {
        boolean success = aboutValueService.deleteValue(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/about-values/{id}/status")
    public Result<String> updateValueStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = aboutValueService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
    
    // ==================== 平台数据管理 ====================
    
    @GetMapping("/about-stats")
    public Result<Page<AboutStat>> getStatList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<AboutStat> page = aboutStatService.getStatPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/about-stats/all")
    public Result<List<AboutStat>> getAllStats() {
        List<AboutStat> list = aboutStatService.getAllStats();
        return Result.success(list);
    }
    
    @GetMapping("/about-stats/{id}")
    public Result<AboutStat> getStatDetail(@PathVariable Long id) {
        AboutStat item = aboutStatService.getStatById(id);
        if (item == null) {
            return Result.error("平台数据不存在");
        }
        return Result.success(item);
    }
    
    @PostMapping("/about-stats")
    public Result<Map<String, Object>> createStat(@RequestBody AboutStat item) {
        boolean success = aboutStatService.createStat(item);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            return Result.success("创建成功", data);
        } else {
            return Result.error("创建失败，请填写必填字段");
        }
    }
    
    @PutMapping("/about-stats/{id}")
    public Result<String> updateStat(@PathVariable Long id, @RequestBody AboutStat item) {
        item.setId(id);
        boolean success = aboutStatService.updateStat(item);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/about-stats/{id}")
    public Result<String> deleteStat(@PathVariable Long id) {
        boolean success = aboutStatService.deleteStat(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/about-stats/{id}/status")
    public Result<String> updateStatStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = aboutStatService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
    
    // ==================== 核心团队管理 ====================
    
    @GetMapping("/about-teams")
    public Result<Page<AboutTeam>> getTeamList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<AboutTeam> page = aboutTeamService.getTeamPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/about-teams/all")
    public Result<List<AboutTeam>> getAllTeams() {
        List<AboutTeam> list = aboutTeamService.getAllTeams();
        return Result.success(list);
    }
    
    @GetMapping("/about-teams/{id}")
    public Result<AboutTeam> getTeamDetail(@PathVariable Long id) {
        AboutTeam item = aboutTeamService.getTeamById(id);
        if (item == null) {
            return Result.error("团队成员不存在");
        }
        return Result.success(item);
    }
    
    @PostMapping("/about-teams")
    public Result<Map<String, Object>> createTeam(@RequestBody AboutTeam item) {
        boolean success = aboutTeamService.createTeam(item);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            return Result.success("创建成功", data);
        } else {
            return Result.error("创建失败，请填写必填字段");
        }
    }
    
    @PutMapping("/about-teams/{id}")
    public Result<String> updateTeam(@PathVariable Long id, @RequestBody AboutTeam item) {
        item.setId(id);
        boolean success = aboutTeamService.updateTeam(item);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/about-teams/{id}")
    public Result<String> deleteTeam(@PathVariable Long id) {
        boolean success = aboutTeamService.deleteTeam(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/about-teams/{id}/status")
    public Result<String> updateTeamStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = aboutTeamService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
}
