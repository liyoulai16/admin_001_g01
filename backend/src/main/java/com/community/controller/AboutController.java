package com.community.controller;

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
@RequestMapping("/api/about")
@CrossOrigin(origins = "*")
public class AboutController {
    
    @Resource
    private AboutMissionService aboutMissionService;
    
    @Resource
    private AboutValueService aboutValueService;
    
    @Resource
    private AboutStatService aboutStatService;
    
    @Resource
    private AboutTeamService aboutTeamService;
    
    @GetMapping("/data")
    public Result<Map<String, Object>> getAboutData() {
        AboutMission mission = aboutMissionService.getMission();
        List<AboutValue> values = aboutValueService.getAllValues();
        List<AboutStat> stats = aboutStatService.getAllStats();
        List<AboutTeam> teams = aboutTeamService.getAllTeams();
        
        Map<String, Object> result = new HashMap<>();
        result.put("mission", mission);
        result.put("values", values);
        result.put("stats", stats);
        result.put("teams", teams);
        
        return Result.success(result);
    }
    
    @GetMapping("/mission")
    public Result<AboutMission> getMission() {
        AboutMission mission = aboutMissionService.getMission();
        if (mission == null) {
            return Result.error("暂无使命数据");
        }
        return Result.success(mission);
    }
    
    @GetMapping("/values")
    public Result<List<AboutValue>> getValues() {
        List<AboutValue> values = aboutValueService.getAllValues();
        return Result.success(values);
    }
    
    @GetMapping("/stats")
    public Result<List<AboutStat>> getStats() {
        List<AboutStat> stats = aboutStatService.getAllStats();
        return Result.success(stats);
    }
    
    @GetMapping("/teams")
    public Result<List<AboutTeam>> getTeams() {
        List<AboutTeam> teams = aboutTeamService.getAllTeams();
        return Result.success(teams);
    }
}
