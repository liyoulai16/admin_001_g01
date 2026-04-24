package com.community.controller;

import com.community.common.Result;
import com.community.entity.Coupon;
import com.community.entity.UserCoupon;
import com.community.service.CouponService;
import com.community.service.UserCouponService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {
    
    @Resource
    private CouponService couponService;
    
    @Resource
    private UserCouponService userCouponService;
    
    @GetMapping("/active")
    public Result<List<Coupon>> getActiveCoupons() {
        List<Coupon> coupons = couponService.getActiveCoupons();
        return Result.success(coupons);
    }
    
    @GetMapping("/my")
    public Result<Map<String, Object>> getMyCoupons(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        if (userId == null) {
            userId = 1L;
        }
        
        var pageResult = userCouponService.getUserCoupons(userId, status, current, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("current", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("pages", pageResult.getPages());
        
        return Result.success(result);
    }
    
    @GetMapping("/available")
    public Result<List<UserCoupon>> getAvailableCoupons(
            @RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        List<UserCoupon> coupons = userCouponService.getAvailableCoupons(userId);
        return Result.success(coupons);
    }
    
    @GetMapping("/for-service")
    public Result<List<UserCoupon>> getCouponsForService(
            @RequestParam(required = false) Long userId,
            @RequestParam BigDecimal servicePrice,
            @RequestParam(required = false) String category) {
        if (userId == null) {
            userId = 1L;
        }
        List<UserCoupon> coupons = userCouponService.getAvailableCouponsForService(userId, servicePrice, category);
        return Result.success(coupons);
    }
    
    @PostMapping("/claim/{couponId}")
    public Result<Map<String, Object>> claimCoupon(
            @PathVariable Long couponId,
            @RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        
        try {
            UserCoupon coupon = userCouponService.claimCoupon(userId, couponId);
            Map<String, Object> result = new HashMap<>();
            result.put("coupon", coupon);
            return Result.success("领取成功", result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/calculate-discount")
    public Result<Map<String, Object>> calculateDiscount(
            @RequestParam Long userCouponId,
            @RequestParam BigDecimal originalPrice) {
        UserCoupon coupon = null;
        Map<String, Object> result = new HashMap<>();
        
        try {
            BigDecimal discount = userCouponService.calculateDiscount(coupon, originalPrice);
            result.put("discount", discount);
            result.put("finalPrice", originalPrice.subtract(discount));
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("计算优惠失败");
        }
    }
}
