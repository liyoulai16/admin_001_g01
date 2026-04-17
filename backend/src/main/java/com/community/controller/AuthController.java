package com.community.controller;

import com.community.common.Result;
import com.community.dto.LoginRequest;
import com.community.dto.RegisterRequest;
import com.community.dto.UpdatePasswordRequest;
import com.community.dto.UpdateUserInfoRequest;
import com.community.entity.User;
import com.community.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
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
    
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam("username") String username) {
        boolean exists = userService.checkUsernameExists(username);
        return Result.success(exists);
    }
    
    @GetMapping("/user-info")
    public Result<Map<String, Object>> getUserInfo(@RequestParam("username") String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("status", user.getStatus());
        userInfo.put("createTime", user.getCreateTime());
        return Result.success(userInfo);
    }
    
    @PostMapping("/update-info")
    public Result<Map<String, Object>> updateUserInfo(@Validated @RequestBody UpdateUserInfoRequest request) {
        Map<String, Object> result = userService.updateUserInfo(
            request.getCurrentUsername(),
            request.getNewUsername(),
            request.getNickname()
        );
        if ((Boolean) result.get("success")) {
            return Result.success((String) result.get("message"), result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @PostMapping("/update-password")
    public Result<Map<String, Object>> updatePassword(@Validated @RequestBody UpdatePasswordRequest request) {
        Map<String, Object> result = userService.updatePassword(
            request.getUsername(),
            request.getOldPassword(),
            request.getNewPassword(),
            request.getConfirmPassword()
        );
        if ((Boolean) result.get("success")) {
            return Result.success((String) result.get("message"));
        } else {
            return Result.error((String) result.get("message"));
        }
    }
}
