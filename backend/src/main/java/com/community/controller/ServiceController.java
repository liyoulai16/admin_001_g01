package com.community.controller;

import com.community.common.Result;
import com.community.entity.ServiceCategory;
import com.community.entity.ServiceItem;
import com.community.service.ServiceCategoryService;
import com.community.service.ServiceItemService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceController {
    
    @Resource
    private ServiceCategoryService serviceCategoryService;
    
    @Resource
    private ServiceItemService serviceItemService;
    
    @GetMapping("/categories")
    public Result<List<ServiceCategory>> getCategories() {
        List<ServiceCategory> categories = serviceCategoryService.getAllCategories();
        return Result.success(categories);
    }
    
    @GetMapping("/list")
    public Result<List<ServiceItem>> getServices() {
        List<ServiceItem> services = serviceItemService.getAllServices();
        return Result.success(services);
    }
    
    @GetMapping("/{id}")
    public Result<ServiceItem> getServiceById(@PathVariable("id") Long id) {
        ServiceItem service = serviceItemService.getServiceById(id);
        if (service == null) {
            return Result.error("服务不存在");
        }
        return Result.success(service);
    }
    
    @GetMapping("/category/{categoryId}")
    public Result<List<ServiceItem>> getServicesByCategoryId(@PathVariable("categoryId") Long categoryId) {
        List<ServiceItem> services = serviceItemService.getServicesByCategoryId(categoryId);
        return Result.success(services);
    }
    
    @GetMapping("/search")
    public Result<Map<String, Object>> searchServices(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "priceRange", required = false) String priceRange,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        
        List<ServiceItem> services = serviceItemService.searchServices(keyword, category, priceRange, sortBy);
        List<ServiceCategory> categories = serviceCategoryService.getAllCategories();
        
        Map<String, Object> result = new HashMap<>();
        result.put("services", services);
        result.put("categories", categories);
        
        return Result.success(result);
    }
}
