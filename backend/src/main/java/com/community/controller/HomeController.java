package com.community.controller;

import com.community.common.Result;
import com.community.entity.Carousel;
import com.community.entity.PlatformIntro;
import com.community.service.CarouselService;
import com.community.service.PlatformIntroService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "*")
public class HomeController {
    
    @Resource
    private CarouselService carouselService;
    
    @Resource
    private PlatformIntroService platformIntroService;
    
    @GetMapping("/carousels")
    public Result<List<Carousel>> getCarousels() {
        List<Carousel> carousels = carouselService.getAllCarousels();
        return Result.success(carousels);
    }
    
    @GetMapping("/intro")
    public Result<PlatformIntro> getPlatformIntro() {
        PlatformIntro intro = platformIntroService.getPlatformIntro();
        if (intro == null) {
            return Result.error("平台简介不存在");
        }
        return Result.success(intro);
    }
    
    @GetMapping("/data")
    public Result<Map<String, Object>> getHomeData() {
        List<Carousel> carousels = carouselService.getAllCarousels();
        PlatformIntro intro = platformIntroService.getPlatformIntro();
        
        Map<String, Object> result = new HashMap<>();
        result.put("carousels", carousels);
        result.put("intro", intro);
        
        return Result.success(result);
    }
}
