package com.community.controller;

import com.community.common.Result;
import com.community.dto.LoginRequest;
import com.community.dto.RegisterRequest;
import com.community.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Resource
    private UserService userService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequest request) {
        Map<String, Object> result = userService.login(request);
        if ((Boolean) result.get("success")) {
            return Result.success((String) result.get("message"), result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Validated @RequestBody RegisterRequest request) {
        Map<String, Object> result = userService.register(request);
        if ((Boolean) result.get("success")) {
            return Result.success((String) result.get("message"), result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务运行正常");
    }
}
