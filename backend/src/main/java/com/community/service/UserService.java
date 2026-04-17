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
    
    public User getUserByUsername(String username) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0)
        );
    }
    
    public Map<String, Object> updateUserInfo(String currentUsername, String newUsername, String nickname) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, currentUsername)
                .eq(User::getDeleted, 0)
        );
        
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        
        if (!currentUsername.equals(newUsername)) {
            User existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, newUsername)
                    .eq(User::getDeleted, 0)
            );
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                result.put("success", false);
                result.put("message", "新用户名已被使用");
                return result;
            }
        }
        
        user.setUsername(newUsername);
        user.setNickname(StringUtils.hasText(nickname) ? nickname : newUsername);
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.updateById(user);
        
        result.put("success", true);
        result.put("message", "修改成功");
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        return result;
    }
    
    public Map<String, Object> updatePassword(String username, String oldPassword, String newPassword, String confirmPassword) {
        Map<String, Object> result = new HashMap<>();
        
        if (!newPassword.equals(confirmPassword)) {
            result.put("success", false);
            result.put("message", "两次输入的新密码不一致");
            return result;
        }
        
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0)
        );
        
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            result.put("success", false);
            result.put("message", "原密码错误");
            return result;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.updateById(user);
        
        result.put("success", true);
        result.put("message", "密码修改成功");
        return result;
    }
}
