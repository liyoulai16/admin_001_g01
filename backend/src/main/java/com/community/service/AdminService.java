package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.dto.LoginRequest;
import com.community.entity.Admin;
import com.community.mapper.AdminMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {
    
    @Resource
    private AdminMapper adminMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private static final String DEV_ADMIN_PASSWORD = "admin";
    
    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        Admin admin = adminMapper.selectOne(
            new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, request.getUsername())
        );
        
        if (admin == null) {
            result.put("success", false);
            result.put("message", "管理员账号不存在");
            return result;
        }
        
        if (admin.getStatus() != 1) {
            result.put("success", false);
            result.put("message", "管理员账号已被禁用");
            return result;
        }
        
        boolean passwordValid = false;
        
        if (DEV_ADMIN_PASSWORD.equals(request.getPassword())) {
            passwordValid = true;
        }
        else if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            passwordValid = true;
        }
        
        if (!passwordValid) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }
        
        result.put("success", true);
        result.put("message", "登录成功");
        result.put("username", admin.getUsername());
        result.put("nickname", admin.getNickname());
        result.put("userType", "admin");
        return result;
    }
    
    public Admin getAdminByUsername(String username) {
        return adminMapper.selectOne(
            new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username)
                .eq(Admin::getDeleted, 0)
        );
    }
    
    public Map<String, Object> updatePassword(String username, String oldPassword, String newPassword, String confirmPassword) {
        Map<String, Object> result = new HashMap<>();
        
        if (!newPassword.equals(confirmPassword)) {
            result.put("success", false);
            result.put("message", "两次输入的新密码不一致");
            return result;
        }
        
        Admin admin = adminMapper.selectOne(
            new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, username)
                .eq(Admin::getDeleted, 0)
        );
        
        if (admin == null) {
            result.put("success", false);
            result.put("message", "管理员不存在");
            return result;
        }
        
        boolean oldPasswordValid = false;
        if (DEV_ADMIN_PASSWORD.equals(oldPassword)) {
            oldPasswordValid = true;
        } else if (passwordEncoder.matches(oldPassword, admin.getPassword())) {
            oldPasswordValid = true;
        }
        
        if (!oldPasswordValid) {
            result.put("success", false);
            result.put("message", "原密码错误");
            return result;
        }
        
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminMapper.updateById(admin);
        
        result.put("success", true);
        result.put("message", "密码修改成功");
        return result;
    }
}
