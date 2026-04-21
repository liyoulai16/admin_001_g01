package com.community.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.context.LoginUserContext;
import com.community.dto.RechargeCreateRequest;
import com.community.dto.RechargeQueryRequest;
import com.community.entity.RechargeRecord;
import com.community.entity.User;
import com.community.service.RechargeService;
import com.community.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/recharge")
@CrossOrigin(origins = "*")
public class RechargeController {
    
    @Resource
    private RechargeService rechargeService;
    
    @Resource
    private UserService userService;
    
    @PostMapping("/create")
    public Result<RechargeRecord> createRecharge(@Validated @RequestBody RechargeCreateRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            RechargeRecord recharge = rechargeService.createRecharge(user.getId(), request);
            return Result.success("充值订单创建成功", recharge);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/pay/{rechargeId}")
    public Result<RechargeRecord> payRecharge(
            @PathVariable("rechargeId") Long rechargeId,
            @RequestParam(value = "transactionId", required = false) String transactionId) {
        
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            RechargeRecord recharge = rechargeService.payRecharge(user.getId(), rechargeId, transactionId);
            return Result.success("充值成功", recharge);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/cancel/{rechargeId}")
    public Result<RechargeRecord> cancelRecharge(@PathVariable("rechargeId") Long rechargeId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            RechargeRecord recharge = rechargeService.cancelRecharge(user.getId(), rechargeId);
            return Result.success("充值订单取消成功", recharge);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{rechargeId}")
    public Result<RechargeRecord> getRechargeById(@PathVariable("rechargeId") Long rechargeId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        RechargeRecord recharge = rechargeService.getRechargeById(user.getId(), rechargeId);
        if (recharge == null) {
            return Result.error("充值记录不存在");
        }
        return Result.success(recharge);
    }
    
    @GetMapping("/list")
    public Result<PageResult<RechargeRecord>> getRechargeList(
            @RequestParam(value = "payStatus", required = false) Integer payStatus,
            @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @RequestParam(value = "size", required = false, defaultValue = "10") long size) {
        
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        RechargeQueryRequest request = new RechargeQueryRequest();
        request.setPayStatus(payStatus);
        request.setCurrent(current);
        request.setSize(size);
        
        PageResult<RechargeRecord> result = rechargeService.getRechargePage(user.getId(), request);
        return Result.success(result);
    }
}
