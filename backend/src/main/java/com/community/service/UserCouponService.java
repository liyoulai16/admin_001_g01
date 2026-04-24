package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.entity.Coupon;
import com.community.entity.User;
import com.community.entity.UserCoupon;
import com.community.mapper.CouponMapper;
import com.community.mapper.UserCouponMapper;
import com.community.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCouponService {
    
    @Resource
    private UserCouponMapper userCouponMapper;
    
    @Resource
    private CouponMapper couponMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private CouponService couponService;
    
    public PageResult<UserCoupon> getUserCoupons(Long userId, String status, long current, long size) {
        Page<UserCoupon> page = new Page<>(current, size);
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        
        if (status != null && !"all".equals(status)) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        
        wrapper.orderByDesc(UserCoupon::getObtainTime);
        Page<UserCoupon> result = userCouponMapper.selectPage(page, wrapper);
        
        return PageResult.of(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
    }
    
    public List<UserCoupon> getAvailableCoupons(Long userId) {
        LocalDate today = LocalDate.now();
        return userCouponMapper.selectList(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, UserCoupon.STATUS_AVAILABLE)
                .le(UserCoupon::getValidFrom, today)
                .ge(UserCoupon::getValidTo, today)
                .orderByDesc(UserCoupon::getObtainTime)
        );
    }
    
    public List<UserCoupon> getAvailableCouponsForService(Long userId, BigDecimal servicePrice, String category) {
        LocalDate today = LocalDate.now();
        List<UserCoupon> coupons = userCouponMapper.selectList(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, UserCoupon.STATUS_AVAILABLE)
                .le(UserCoupon::getValidFrom, today)
                .ge(UserCoupon::getValidTo, today)
                .orderByDesc(UserCoupon::getObtainTime)
        );
        
        return coupons.stream()
            .filter(coupon -> {
                if (coupon.getCategory() != null && !coupon.getCategory().isEmpty()) {
                    if (category == null || !coupon.getCategory().equals(category)) {
                        return false;
                    }
                }
                if (coupon.getMinSpend() != null && coupon.getMinSpend().compareTo(BigDecimal.ZERO) > 0) {
                    if (servicePrice.compareTo(coupon.getMinSpend()) < 0) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }
    
    @Transactional
    public UserCoupon claimCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        
        if (coupon.getStatus() != Coupon.STATUS_ACTIVE) {
            throw new RuntimeException("优惠券不可领取");
        }
        
        LocalDate today = LocalDate.now();
        if (today.isBefore(coupon.getValidFrom()) || today.isAfter(coupon.getValidTo())) {
            throw new RuntimeException("优惠券不在有效期内");
        }
        
        if (coupon.getClaimedQuantity() >= coupon.getTotalQuantity()) {
            throw new RuntimeException("优惠券已被领完");
        }
        
        if (coupon.getPerUserLimit() != null && coupon.getPerUserLimit() > 0) {
            Long claimedCount = userCouponMapper.selectCount(
                new LambdaQueryWrapper<UserCoupon>()
                    .eq(UserCoupon::getUserId, userId)
                    .eq(UserCoupon::getCouponId, couponId)
            );
            if (claimedCount >= coupon.getPerUserLimit()) {
                throw new RuntimeException("您已领取过该优惠券");
            }
        }
        
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setName(coupon.getName());
        userCoupon.setType(coupon.getType());
        userCoupon.setDiscountAmount(coupon.getDiscountAmount());
        userCoupon.setDiscountPercent(coupon.getDiscountPercent());
        userCoupon.setMaxDiscount(coupon.getMaxDiscount());
        userCoupon.setMinSpend(coupon.getMinSpend());
        userCoupon.setValidFrom(coupon.getValidFrom());
        userCoupon.setValidTo(coupon.getValidTo());
        userCoupon.setCategory(coupon.getCategory());
        userCoupon.setDescription(coupon.getDescription());
        userCoupon.setStatus(UserCoupon.STATUS_AVAILABLE);
        userCoupon.setObtainTime(LocalDateTime.now());
        userCoupon.setDeleted(0);
        
        userCouponMapper.insert(userCoupon);
        
        coupon.setClaimedQuantity(coupon.getClaimedQuantity() + 1);
        couponMapper.updateById(coupon);
        
        return userCoupon;
    }
    
    @Transactional
    public UserCoupon claimRandomBrowseReward(Long userId) {
        Coupon coupon = couponService.getRandomBrowseRewardCoupon();
        if (coupon == null) {
            return null;
        }
        return claimCoupon(userId, coupon.getId());
    }
    
    @Transactional
    public boolean useCoupon(Long userCouponId, Long orderId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            return false;
        }
        
        if (!UserCoupon.STATUS_AVAILABLE.equals(userCoupon.getStatus())) {
            return false;
        }
        
        userCoupon.setStatus(UserCoupon.STATUS_USED);
        userCoupon.setUseTime(LocalDateTime.now());
        userCoupon.setOrderId(orderId);
        
        return userCouponMapper.updateById(userCoupon) > 0;
    }
    
    public BigDecimal calculateDiscount(UserCoupon coupon, BigDecimal originalPrice) {
        if (coupon == null || originalPrice == null) {
            return BigDecimal.ZERO;
        }
        
        if (Coupon.TYPE_FULL_REDUCTION.equals(coupon.getType())) {
            if (coupon.getMinSpend() != null && originalPrice.compareTo(coupon.getMinSpend()) >= 0) {
                return coupon.getDiscountAmount() != null ? coupon.getDiscountAmount() : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }
        
        if (Coupon.TYPE_DISCOUNT.equals(coupon.getType())) {
            if (coupon.getDiscountPercent() == null) {
                return BigDecimal.ZERO;
            }
            BigDecimal discount = originalPrice.multiply(BigDecimal.ONE.subtract(coupon.getDiscountPercent()));
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
            return discount.setScale(2, java.math.RoundingMode.HALF_UP);
        }
        
        return BigDecimal.ZERO;
    }
}
