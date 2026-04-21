package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.entity.BalanceTransaction;
import com.community.entity.UserBalance;
import com.community.mapper.BalanceTransactionMapper;
import com.community.mapper.UserBalanceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class UserBalanceService {
    
    @Resource
    private UserBalanceMapper userBalanceMapper;
    
    @Resource
    private BalanceTransactionMapper balanceTransactionMapper;
    
    public UserBalance getOrCreateBalance(Long userId) {
        UserBalance balance = userBalanceMapper.selectOne(
            new LambdaQueryWrapper<UserBalance>()
                .eq(UserBalance::getUserId, userId)
                .eq(UserBalance::getDeleted, 0)
        );
        
        if (balance == null) {
            balance = new UserBalance();
            balance.setUserId(userId);
            balance.setBalance(BigDecimal.ZERO);
            balance.setFrozenBalance(BigDecimal.ZERO);
            balance.setTotalRecharge(BigDecimal.ZERO);
            balance.setTotalSpend(BigDecimal.ZERO);
            balance.setStatus(1);
            balance.setDeleted(0);
            balance.setCreateTime(LocalDateTime.now());
            balance.setUpdateTime(LocalDateTime.now());
            userBalanceMapper.insert(balance);
        }
        
        return balance;
    }
    
    public UserBalance getBalance(Long userId) {
        return userBalanceMapper.selectOne(
            new LambdaQueryWrapper<UserBalance>()
                .eq(UserBalance::getUserId, userId)
                .eq(UserBalance::getDeleted, 0)
        );
    }
    
    @Transactional
    public void addBalance(Long userId, BigDecimal amount, Long relatedId, String relatedType, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        UserBalance balance = getOrCreateBalance(userId);
        BigDecimal balanceBefore = balance.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        
        LambdaUpdateWrapper<UserBalance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserBalance::getId, balance.getId())
                    .set(UserBalance::getBalance, balanceAfter)
                    .set(UserBalance::getUpdateTime, LocalDateTime.now());
        
        if ("recharge".equals(relatedType)) {
            updateWrapper.setSql("total_recharge = total_recharge + " + amount);
        }
        
        userBalanceMapper.update(null, updateWrapper);
        
        createTransaction(userId, 1, amount, balanceBefore, balanceAfter, relatedId, relatedType, description, 1);
    }
    
    @Transactional
    public void deductBalance(Long userId, BigDecimal amount, Long relatedId, String relatedType, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        UserBalance balance = getOrCreateBalance(userId);
        if (balance.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        BigDecimal balanceBefore = balance.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(amount);
        
        LambdaUpdateWrapper<UserBalance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserBalance::getId, balance.getId())
                    .set(UserBalance::getBalance, balanceAfter)
                    .setSql("total_spend = total_spend + " + amount)
                    .set(UserBalance::getUpdateTime, LocalDateTime.now());
        
        userBalanceMapper.update(null, updateWrapper);
        
        createTransaction(userId, 2, amount, balanceBefore, balanceAfter, relatedId, relatedType, description, 1);
    }
    
    @Transactional
    public void refundBalance(Long userId, BigDecimal amount, Long relatedId, String relatedType, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        
        UserBalance balance = getOrCreateBalance(userId);
        BigDecimal balanceBefore = balance.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        
        LambdaUpdateWrapper<UserBalance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserBalance::getId, balance.getId())
                    .set(UserBalance::getBalance, balanceAfter)
                    .set(UserBalance::getUpdateTime, LocalDateTime.now());
        
        userBalanceMapper.update(null, updateWrapper);
        
        createTransaction(userId, 3, amount, balanceBefore, balanceAfter, relatedId, relatedType, description, 1);
    }
    
    public PageResult<BalanceTransaction> getTransactionPage(Long userId, Integer type, long current, long size) {
        Page<BalanceTransaction> page = new Page<>(current, size);
        LambdaQueryWrapper<BalanceTransaction> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(BalanceTransaction::getUserId, userId)
                   .eq(BalanceTransaction::getDeleted, 0);
        
        if (type != null) {
            queryWrapper.eq(BalanceTransaction::getType, type);
        }
        
        queryWrapper.orderByDesc(BalanceTransaction::getCreateTime);
        
        Page<BalanceTransaction> resultPage = balanceTransactionMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize()
        );
    }
    
    private void createTransaction(Long userId, Integer type, BigDecimal amount, 
            BigDecimal balanceBefore, BigDecimal balanceAfter, Long relatedId, 
            String relatedType, String description, Integer status) {
        
        BalanceTransaction transaction = new BalanceTransaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setRelatedId(relatedId);
        transaction.setRelatedType(relatedType);
        transaction.setDescription(description);
        transaction.setStatus(status);
        transaction.setDeleted(0);
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());
        
        balanceTransactionMapper.insert(transaction);
    }
    
    private String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "TXN" + timestamp + random;
    }
}
