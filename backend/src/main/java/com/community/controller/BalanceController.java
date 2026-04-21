package com.community.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.context.LoginUserContext;
import com.community.entity.BalanceTransaction;
import com.community.entity.User;
import com.community.entity.UserBalance;
import com.community.service.UserBalanceService;
import com.community.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/balance")
@CrossOrigin(origins = "*")
public class BalanceController {
    
    @Resource
    private UserBalanceService userBalanceService;
    
    @Resource
    private UserService userService;
    
    @GetMapping("/info")
    public Result<Map<String, Object>> getBalanceInfo() {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        UserBalance balance = userBalanceService.getOrCreateBalance(user.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("balance", balance.getBalance());
        result.put("frozenBalance", balance.getFrozenBalance());
        result.put("totalRecharge", balance.getTotalRecharge());
        result.put("totalSpend", balance.getTotalSpend());
        
        return Result.success(result);
    }
    
    @GetMapping("/transactions")
    public Result<PageResult<BalanceTransaction>> getTransactionList(
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @RequestParam(value = "size", required = false, defaultValue = "10") long size) {
        
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        PageResult<BalanceTransaction> result = userBalanceService.getTransactionPage(user.getId(), type, current, size);
        return Result.success(result);
    }
}
