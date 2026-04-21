package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.dto.RechargeCreateRequest;
import com.community.dto.RechargeQueryRequest;
import com.community.entity.BalanceTransaction;
import com.community.entity.RechargeRecord;
import com.community.entity.UserBalance;
import com.community.mapper.BalanceTransactionMapper;
import com.community.mapper.RechargeRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class RechargeService {
    
    @Resource
    private RechargeRecordMapper rechargeRecordMapper;
    
    @Resource
    private UserBalanceService userBalanceService;
    
    @Resource
    private BalanceTransactionMapper balanceTransactionMapper;
    
    @Transactional
    public RechargeRecord createRecharge(Long userId, RechargeCreateRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }
        
        RechargeRecord recharge = new RechargeRecord();
        recharge.setRechargeNo(generateRechargeNo());
        recharge.setUserId(userId);
        recharge.setAmount(request.getAmount());
        recharge.setPayAmount(request.getAmount());
        recharge.setBonusAmount(calculateBonus(request.getAmount()));
        recharge.setPayMethod(request.getPayMethod() != null ? request.getPayMethod() : 0);
        recharge.setPayStatus(0);
        recharge.setStatus(1);
        recharge.setDeleted(0);
        recharge.setCreateTime(LocalDateTime.now());
        recharge.setUpdateTime(LocalDateTime.now());
        
        rechargeRecordMapper.insert(recharge);
        
        return recharge;
    }
    
    @Transactional
    public RechargeRecord payRecharge(Long userId, Long rechargeId, String transactionId) {
        RechargeRecord recharge = rechargeRecordMapper.selectById(rechargeId);
        if (recharge == null || recharge.getDeleted() == 1) {
            throw new RuntimeException("充值记录不存在");
        }
        
        if (!recharge.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此充值记录");
        }
        
        if (recharge.getPayStatus() == 1) {
            throw new RuntimeException("充值已支付");
        }
        
        BigDecimal totalAmount = recharge.getAmount().add(recharge.getBonusAmount() != null ? recharge.getBonusAmount() : BigDecimal.ZERO);
        
        userBalanceService.addBalance(userId, totalAmount, rechargeId, "recharge", "账户充值：" + recharge.getAmount() + "元");
        
        recharge.setPayStatus(1);
        recharge.setPayTime(LocalDateTime.now());
        recharge.setTransactionId(transactionId);
        recharge.setUpdateTime(LocalDateTime.now());
        
        rechargeRecordMapper.updateById(recharge);
        
        return recharge;
    }
    
    @Transactional
    public RechargeRecord cancelRecharge(Long userId, Long rechargeId) {
        RechargeRecord recharge = rechargeRecordMapper.selectById(rechargeId);
        if (recharge == null || recharge.getDeleted() == 1) {
            throw new RuntimeException("充值记录不存在");
        }
        
        if (!recharge.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此充值记录");
        }
        
        if (recharge.getPayStatus() == 1) {
            throw new RuntimeException("充值已支付，无法取消");
        }
        
        recharge.setPayStatus(3);
        recharge.setUpdateTime(LocalDateTime.now());
        
        rechargeRecordMapper.updateById(recharge);
        
        return recharge;
    }
    
    public RechargeRecord getRechargeById(Long userId, Long rechargeId) {
        RechargeRecord recharge = rechargeRecordMapper.selectById(rechargeId);
        if (recharge == null || recharge.getDeleted() == 1) {
            return null;
        }
        if (!recharge.getUserId().equals(userId)) {
            return null;
        }
        return recharge;
    }
    
    public PageResult<RechargeRecord> getRechargePage(Long userId, RechargeQueryRequest request) {
        Page<RechargeRecord> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<RechargeRecord> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(RechargeRecord::getUserId, userId)
                   .eq(RechargeRecord::getDeleted, 0);
        
        if (request.getPayStatus() != null) {
            queryWrapper.eq(RechargeRecord::getPayStatus, request.getPayStatus());
        }
        
        queryWrapper.orderByDesc(RechargeRecord::getCreateTime);
        
        Page<RechargeRecord> resultPage = rechargeRecordMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize()
        );
    }
    
    private BigDecimal calculateBonus(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("100")) >= 0 && amount.compareTo(new BigDecimal("200")) < 0) {
            return new BigDecimal("5");
        } else if (amount.compareTo(new BigDecimal("200")) >= 0 && amount.compareTo(new BigDecimal("500")) < 0) {
            return new BigDecimal("20");
        } else if (amount.compareTo(new BigDecimal("500")) >= 0 && amount.compareTo(new BigDecimal("1000")) < 0) {
            return new BigDecimal("60");
        } else if (amount.compareTo(new BigDecimal("1000")) >= 0) {
            return new BigDecimal("150");
        }
        return BigDecimal.ZERO;
    }
    
    private String generateRechargeNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "RC" + timestamp + random;
    }
}
