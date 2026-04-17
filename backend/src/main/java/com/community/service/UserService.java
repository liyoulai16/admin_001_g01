package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.dto.LoginRequest;
import com.community.dto.RegisterRequest;
import com.community.entity.User;
import com.community.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    
    @Resource
    private UserMapper userMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
        );
        
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户名不存在");
            return result;
        }
        
        if (user.getStatus() != 1) {
            result.put("success", false);
            result.put("message", "账号已被禁用");
            return result;
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }
        
        result.put("success", true);
        result.put("message", "登录成功");
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        return result;
    }
    
    public Map<String, Object> register(RegisterRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.put("success", false);
            result.put("message", "两次输入的密码不一致");
            return result;
        }
        
        User existingUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
        );
        
        if (existingUser != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        result.put("success", true);
        result.put("message", "注册成功");
        result.put("username", user.getUsername());
        return result;
    }
    
    public boolean checkUsernameExists(String username) {
        User existingUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
        );
        return existingUser != null;
    }
}
