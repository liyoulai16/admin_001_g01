package com.community.controller;

import com.community.common.Result;
import com.community.context.LoginUserContext;
import com.community.dto.LoginRequest;
import com.community.dto.RegisterRequest;
import com.community.dto.UpdatePasswordRequest;
import com.community.dto.UpdateUserInfoRequest;
import com.community.entity.User;
import com.community.service.UserService;
import com.community.util.JwtUtils;
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
    
    @Resource
    private JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequest request) {
        Map<String, Object> result = userService.login(request);
        if ((Boolean) result.get("success")) {
            String username = (String) result.get("username");
            String token = jwtUtils.generateToken(username);
            result.put("token", token);
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
    public Result<Map<String, Object>> getUserInfo() {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("status", user.getStatus());
        userInfo.put("createTime", user.getCreateTime());
        return Result.success(userInfo);
    }
    
    @PostMapping("/update-info")
    public Result<Map<String, Object>> updateUserInfo(@Validated @RequestBody UpdateUserInfoRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        Map<String, Object> result = userService.updateUserInfo(
            currentUsername,
            request.getNewUsername(),
            request.getNickname(),
            request.getPhone(),
            request.getEmail()
        );
        if ((Boolean) result.get("success")) {
            String newUsername = (String) result.get("username");
            if (!currentUsername.equals(newUsername)) {
                String newToken = jwtUtils.generateToken(newUsername);
                result.put("token", newToken);
            }
            return Result.success((String) result.get("message"), result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
    
    @PostMapping("/update-password")
    public Result<Map<String, Object>> updatePassword(@Validated @RequestBody UpdatePasswordRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        Map<String, Object> result = userService.updatePassword(
            currentUsername,
            request.getOldPassword(),
            request.getNewPassword(),
            request.getConfirmPassword()
        );
        if ((Boolean) result.get("success")) {
            return Result.success((String) result.get("message"), null);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
}
