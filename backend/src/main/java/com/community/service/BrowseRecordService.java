package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.entity.BrowseRecord;
import com.community.entity.Coupon;
import com.community.entity.UserCoupon;
import com.community.mapper.BrowseRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class BrowseRecordService {
    
    public static final int REQUIRED_SECONDS = 60;
    
    @Resource
    private BrowseRecordMapper browseRecordMapper;
    
    @Resource
    private UserCouponService userCouponService;
    
    public BrowseRecord getOrCreateRecord(Long userId, LocalDate date) {
        BrowseRecord record = browseRecordMapper.selectOne(
            new LambdaQueryWrapper<BrowseRecord>()
                .eq(BrowseRecord::getUserId, userId)
                .eq(BrowseRecord::getBrowseDate, date)
        );
        
        if (record == null) {
            record = new BrowseRecord();
            record.setUserId(userId);
            record.setBrowseDate(date);
            record.setTotalSeconds(0);
            record.setRewardClaimed(false);
            record.setLastActivityTime(LocalDateTime.now());
            record.setDeleted(0);
            browseRecordMapper.insert(record);
        }
        
        return record;
    }
    
    public Map<String, Object> updateBrowsingTime(Long userId, int seconds) {
        LocalDate today = LocalDate.now();
        BrowseRecord record = getOrCreateRecord(userId, today);
        
        int newTotal = record.getTotalSeconds() + seconds;
        record.setTotalSeconds(newTotal);
        record.setLastActivityTime(LocalDateTime.now());
        browseRecordMapper.updateById(record);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalSeconds", newTotal);
        result.put("requiredSeconds", REQUIRED_SECONDS);
        result.put("canClaimReward", newTotal >= REQUIRED_SECONDS && !record.getRewardClaimed());
        result.put("rewardClaimed", record.getRewardClaimed());
        result.put("isFirstTimeToday", !record.getRewardClaimed());
        
        return result;
    }
    
    public Map<String, Object> getBrowsingStatus(Long userId) {
        LocalDate today = LocalDate.now();
        BrowseRecord record = getOrCreateRecord(userId, today);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalSeconds", record.getTotalSeconds());
        result.put("requiredSeconds", REQUIRED_SECONDS);
        result.put("canClaimReward", record.getTotalSeconds() >= REQUIRED_SECONDS && !record.getRewardClaimed());
        result.put("rewardClaimed", record.getRewardClaimed());
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> claimBrowseReward(Long userId) {
        LocalDate today = LocalDate.now();
        BrowseRecord record = getOrCreateRecord(userId, today);
        
        Map<String, Object> result = new HashMap<>();
        
        if (record.getRewardClaimed()) {
            result.put("success", false);
            result.put("message", "今日已领取过浏览奖励");
            return result;
        }
        
        if (record.getTotalSeconds() < REQUIRED_SECONDS) {
            result.put("success", false);
            result.put("message", "浏览时长不足，需要满" + REQUIRED_SECONDS + "秒");
            result.put("currentSeconds", record.getTotalSeconds());
            result.put("requiredSeconds", REQUIRED_SECONDS);
            return result;
        }
        
        UserCoupon coupon = userCouponService.claimRandomBrowseReward(userId);
        
        if (coupon == null) {
            result.put("success", false);
            result.put("message", "暂无可用的浏览奖励优惠券");
            return result;
        }
        
        record.setRewardClaimed(true);
        browseRecordMapper.updateById(record);
        
        result.put("success", true);
        result.put("message", "领取成功");
        result.put("coupon", coupon);
        
        return result;
    }
}
