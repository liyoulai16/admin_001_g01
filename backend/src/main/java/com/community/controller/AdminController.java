package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.Carousel;
import com.community.entity.PlatformIntro;
import com.community.entity.ServiceCategory;
import com.community.entity.ServiceItem;
import com.community.service.CarouselService;
import com.community.service.PlatformIntroService;
import com.community.service.ServiceCategoryService;
import com.community.service.ServiceItemService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Resource
    private CarouselService carouselService;
    
    @Resource
    private PlatformIntroService platformIntroService;
    
    @Resource
    private ServiceCategoryService serviceCategoryService;
    
    @Resource
    private ServiceItemService serviceItemService;
    
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
    
    @GetMapping("/service-categories")
    public Result<Page<ServiceCategory>> getCategoryList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<ServiceCategory> page = serviceCategoryService.getCategoryPage(pageNum, pageSize, keyword);
        return Result.success(page);
    }
    
    @GetMapping("/service-categories/all")
    public Result<List<ServiceCategory>> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryService.getAllCategories();
        return Result.success(categories);
    }
    
    @GetMapping("/service-categories/{id}")
    public Result<ServiceCategory> getCategoryDetail(@PathVariable Long id) {
        ServiceCategory category = serviceCategoryService.getCategoryById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }
    
    @PostMapping("/service-categories")
    public Result<Map<String, Object>> createCategory(@RequestBody ServiceCategory category) {
        boolean success = serviceCategoryService.createCategory(category);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("id", category.getId());
            return Result.success("创建成功", result);
        } else {
            return Result.error("创建失败，分类名称可能已存在");
        }
    }
    
    @PutMapping("/service-categories/{id}")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody ServiceCategory category) {
        category.setId(id);
        boolean success = serviceCategoryService.updateCategory(category);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败，分类名称可能已存在");
        }
    }
    
    @DeleteMapping("/service-categories/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        boolean success = serviceCategoryService.deleteCategory(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败，该分类下可能还有服务");
        }
    }
    
    @GetMapping("/services")
    public Result<Page<ServiceItem>> getServiceList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        Page<ServiceItem> page = serviceItemService.getServicePage(pageNum, pageSize, keyword, categoryId);
        return Result.success(page);
    }
    
    @GetMapping("/services/{id}")
    public Result<ServiceItem> getServiceDetail(@PathVariable Long id) {
        ServiceItem service = serviceItemService.getServiceById(id);
        if (service == null) {
            return Result.error("服务不存在");
        }
        return Result.success(service);
    }
    
    @PostMapping("/services")
    public Result<Map<String, Object>> createService(@RequestBody ServiceItem service) {
        boolean success = serviceItemService.createService(service);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("id", service.getId());
            return Result.success("创建成功", result);
        } else {
            return Result.error("创建失败，请检查必填字段");
        }
    }
    
    @PutMapping("/services/{id}")
    public Result<String> updateService(@PathVariable Long id, @RequestBody ServiceItem service) {
        service.setId(id);
        boolean success = serviceItemService.updateService(service);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/services/{id}")
    public Result<String> deleteService(@PathVariable Long id) {
        boolean success = serviceItemService.deleteService(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
