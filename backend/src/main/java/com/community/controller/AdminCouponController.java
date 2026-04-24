package com.community.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.entity.Coupon;
import com.community.service.CouponService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminCouponController {
    
    @Resource
    private CouponService couponService;
    
    @GetMapping("/coupons")
    public Result<Map<String, Object>> getCouponList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        PageResult<Coupon> pageResult = couponService.listCoupons(current, size, keyword, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("current", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("pages", pageResult.getPages());
        
        return Result.success(result);
    }
    
    @GetMapping("/coupons/{id}")
    public Result<Coupon> getCouponDetail(@PathVariable Long id) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        return Result.success(coupon);
    }
    
    @PostMapping("/coupons")
    public Result<Map<String, Object>> createCoupon(@RequestBody Coupon coupon) {
        boolean success = couponService.createCoupon(coupon);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("id", coupon.getId());
            return Result.success("创建成功", result);
        } else {
            return Result.error("创建失败，请检查必填字段");
        }
    }
    
    @PutMapping("/coupons/{id}")
    public Result<String> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        boolean success = couponService.updateCoupon(coupon);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/coupons/{id}")
    public Result<String> deleteCoupon(@PathVariable Long id) {
        boolean success = couponService.deleteCoupon(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/coupons/{id}/status")
    public Result<String> updateCouponStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        boolean success = couponService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }
}
