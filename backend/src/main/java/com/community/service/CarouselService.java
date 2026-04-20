package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.Carousel;
import com.community.mapper.CarouselMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarouselService {
    
    @Resource
    private CarouselMapper carouselMapper;
    
    public List<Carousel> getAllCarousels() {
        return carouselMapper.selectList(
            new LambdaQueryWrapper<Carousel>()
                .eq(Carousel::getStatus, 1)
                .eq(Carousel::getDeleted, 0)
                .orderByAsc(Carousel::getSortOrder)
        );
    }
    
    public Carousel getCarouselById(Long id) {
        return carouselMapper.selectById(id);
    }
    
    public Page<Carousel> getCarouselPage(int pageNum, int pageSize) {
        Page<Carousel> page = new Page<>(pageNum, pageSize);
        return carouselMapper.selectPage(page,
            new LambdaQueryWrapper<Carousel>()
                .eq(Carousel::getDeleted, 0)
                .orderByAsc(Carousel::getSortOrder)
        );
    }
    
    public boolean createCarousel(Carousel carousel) {
        if (!StringUtils.hasText(carousel.getTitle())) {
            return false;
        }
        if (!StringUtils.hasText(carousel.getImageUrl())) {
            return false;
        }
        carousel.setStatus(1);
        carousel.setDeleted(0);
        carousel.setCreateTime(LocalDateTime.now());
        carousel.setUpdateTime(LocalDateTime.now());
        return carouselMapper.insert(carousel) > 0;
    }
    
    public boolean updateCarousel(Carousel carousel) {
        Carousel existing = carouselMapper.selectById(carousel.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (StringUtils.hasText(carousel.getTitle())) {
            existing.setTitle(carousel.getTitle());
        }
        if (carousel.getDescription() != null) {
            existing.setDescription(carousel.getDescription());
        }
        if (StringUtils.hasText(carousel.getImageUrl())) {
            existing.setImageUrl(carousel.getImageUrl());
        }
        if (carousel.getCategory() != null) {
            existing.setCategory(carousel.getCategory());
        }
        if (carousel.getSortOrder() != null) {
            existing.setSortOrder(carousel.getSortOrder());
        }
        if (carousel.getStatus() != null) {
            existing.setStatus(carousel.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return carouselMapper.updateById(existing) > 0;
    }
    
    public boolean deleteCarousel(Long id) {
        Carousel existing = carouselMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return carouselMapper.updateById(existing) > 0;
    }
}
