package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.entity.Coupon;
import com.community.mapper.CouponMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Service
public class CouponService {
    
    @Resource
    private CouponMapper couponMapper;
    
    public PageResult<Coupon> listCoupons(long current, long size, String keyword, Integer status) {
        Page<Coupon> page = new Page<>(current, size);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Coupon::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        }
        
        wrapper.orderByDesc(Coupon::getCreateTime);
        Page<Coupon> result = couponMapper.selectPage(page, wrapper);
        
        return new PageResult<>(
            result.getCurrent(),
            result.getSize(),
            result.getTotal(),
            result.getPages(),
            result.getRecords()
        );
    }
    
    public Coupon getCouponById(Long id) {
        return couponMapper.selectById(id);
    }
    
    public boolean createCoupon(Coupon coupon) {
        coupon.setStatus(Coupon.STATUS_DRAFT);
        coupon.setClaimedQuantity(0);
        coupon.setDeleted(0);
        return couponMapper.insert(coupon) > 0;
    }
    
    public boolean updateCoupon(Coupon coupon) {
        Coupon existing = couponMapper.selectById(coupon.getId());
        if (existing == null) {
            return false;
        }
        return couponMapper.updateById(coupon) > 0;
    }
    
    public boolean deleteCoupon(Long id) {
        return couponMapper.deleteById(id) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setStatus(status);
        return couponMapper.updateById(coupon) > 0;
    }
    
    public List<Coupon> getActiveCoupons() {
        LocalDate now = LocalDate.now();
        return couponMapper.selectList(
            new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, Coupon.STATUS_ACTIVE)
                .le(Coupon::getValidFrom, now)
                .ge(Coupon::getValidTo, now)
                .gt(Coupon::getTotalQuantity, 0)
                .orderByAsc(Coupon::getSortOrder)
        );
    }
    
    public Coupon getRandomBrowseRewardCoupon() {
        List<Coupon> coupons = couponMapper.selectList(
            new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, Coupon.STATUS_ACTIVE)
                .eq(Coupon::getCategory, "浏览奖励")
                .le(Coupon::getValidFrom, LocalDate.now())
                .ge(Coupon::getValidTo, LocalDate.now())
                .gt(Coupon::getTotalQuantity, 0)
        );
        
        if (coupons.isEmpty()) {
            return null;
        }
        
        int randomIndex = (int) (Math.random() * coupons.size());
        return coupons.get(randomIndex);
    }
}
