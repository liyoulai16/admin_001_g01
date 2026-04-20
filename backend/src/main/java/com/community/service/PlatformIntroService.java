package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.entity.PlatformIntro;
import com.community.mapper.PlatformIntroMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

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
    
    public PlatformIntro getPlatformIntroById(Long id) {
        return platformIntroMapper.selectById(id);
    }
    
    public boolean createPlatformIntro(PlatformIntro intro) {
        intro.setStatus(1);
        intro.setDeleted(0);
        intro.setCreateTime(LocalDateTime.now());
        intro.setUpdateTime(LocalDateTime.now());
        return platformIntroMapper.insert(intro) > 0;
    }
    
    public boolean updatePlatformIntro(PlatformIntro intro) {
        PlatformIntro existing = null;
        
        if (intro.getId() != null) {
            existing = platformIntroMapper.selectById(intro.getId());
        } else {
            existing = getPlatformIntro();
        }
        
        if (existing != null && existing.getDeleted() != 1) {
            if (StringUtils.hasText(intro.getTitle())) {
                existing.setTitle(intro.getTitle());
            }
            if (intro.getContent() != null) {
                existing.setContent(intro.getContent());
            }
            if (intro.getImageUrl() != null) {
                existing.setImageUrl(intro.getImageUrl());
            }
            if (intro.getStat1Label() != null) {
                existing.setStat1Label(intro.getStat1Label());
            }
            if (intro.getStat1Value() != null) {
                existing.setStat1Value(intro.getStat1Value());
            }
            if (intro.getStat2Label() != null) {
                existing.setStat2Label(intro.getStat2Label());
            }
            if (intro.getStat2Value() != null) {
                existing.setStat2Value(intro.getStat2Value());
            }
            if (intro.getStat3Label() != null) {
                existing.setStat3Label(intro.getStat3Label());
            }
            if (intro.getStat3Value() != null) {
                existing.setStat3Value(intro.getStat3Value());
            }
            if (intro.getStat4Label() != null) {
                existing.setStat4Label(intro.getStat4Label());
            }
            if (intro.getStat4Value() != null) {
                existing.setStat4Value(intro.getStat4Value());
            }
            if (intro.getStatus() != null) {
                existing.setStatus(intro.getStatus());
            }
            existing.setUpdateTime(LocalDateTime.now());
            return platformIntroMapper.updateById(existing) > 0;
        } else {
            return createPlatformIntro(intro);
        }
    }
}
