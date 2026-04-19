package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.entity.Carousel;
import com.community.mapper.CarouselMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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
}
