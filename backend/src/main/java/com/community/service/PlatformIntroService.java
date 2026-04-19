package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.entity.PlatformIntro;
import com.community.mapper.PlatformIntroMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class PlatformIntroService {
    
    @Resource
    private PlatformIntroMapper platformIntroMapper;
    
    public PlatformIntro getPlatformIntro() {
        return platformIntroMapper.selectOne(
            new LambdaQueryWrapper<PlatformIntro>()
                .eq(PlatformIntro::getStatus, 1)
                .eq(PlatformIntro::getDeleted, 0)
                .orderByDesc(PlatformIntro::getCreateTime)
                .last("LIMIT 1")
        );
    }
}
