package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.Carousel;
import com.community.entity.PlatformIntro;
import com.community.service.CarouselService;
import com.community.service.PlatformIntroService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Resource
    private CarouselService carouselService;
    
    @Resource
    private PlatformIntroService platformIntroService;
    
    @GetMapping("/carousels")
    public Result<Page<Carousel>> getCarouselList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Carousel> page = carouselService.getCarouselPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/carousels/{id}")
    public Result<Carousel> getCarouselDetail(@PathVariable Long id) {
        Carousel carousel = carouselService.getCarouselById(id);
        if (carousel == null) {
            return Result.error("轮播图不存在");
        }
        return Result.success(carousel);
    }
    
    @PostMapping("/carousels")
    public Result<Map<String, Object>> createCarousel(@RequestBody Carousel carousel) {
        boolean success = carouselService.createCarousel(carousel);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("id", carousel.getId());
            return Result.success("创建成功", result);
        } else {
            return Result.error("创建失败，请检查必填字段");
        }
    }
    
    @PutMapping("/carousels/{id}")
    public Result<String> updateCarousel(@PathVariable Long id, @RequestBody Carousel carousel) {
        carousel.setId(id);
        boolean success = carouselService.updateCarousel(carousel);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/carousels/{id}")
    public Result<String> deleteCarousel(@PathVariable Long id) {
        boolean success = carouselService.deleteCarousel(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @GetMapping("/intro")
    public Result<PlatformIntro> getPlatformIntro() {
        PlatformIntro intro = platformIntroService.getPlatformIntro();
        return Result.success(intro);
    }
    
    @PutMapping("/intro")
    public Result<String> updatePlatformIntro(@RequestBody PlatformIntro intro) {
        boolean success = platformIntroService.updatePlatformIntro(intro);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
}
