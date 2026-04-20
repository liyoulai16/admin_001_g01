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
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
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
}
