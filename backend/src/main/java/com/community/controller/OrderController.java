package com.community.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.context.LoginUserContext;
import com.community.dto.*;
import com.community.entity.*;
import com.community.service.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Resource
    private ServiceOrderService serviceOrderService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private OrderReviewService orderReviewService;
    
    @PostMapping("/create")
    public Result<ServiceOrder> createOrder(@Validated @RequestBody OrderCreateRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceOrder order = serviceOrderService.createOrder(user.getId(), request);
            return Result.success("订单创建成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/pay")
    public Result<ServiceOrder> payOrder(@Validated @RequestBody OrderPayRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceOrder order = serviceOrderService.payOrder(user.getId(), request.getOrderId(), request.getPayMethod(), request.getTransactionId());
            return Result.success("支付成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/cancel")
    public Result<ServiceOrder> cancelOrder(@Validated @RequestBody OrderCancelRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceOrder order = serviceOrderService.cancelOrder(user.getId(), request.getOrderId(), request.getCancelReason());
            return Result.success("订单取消成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{orderId}")
    public Result<ServiceOrder> getOrderById(@PathVariable("orderId") Long orderId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        ServiceOrder order = serviceOrderService.getOrderWithStatusUpdate(user.getId(), orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }
    
    @PostMapping("/refund")
    public Result<ServiceOrder> requestRefund(@Validated @RequestBody OrderRefundRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceOrder order = serviceOrderService.requestRefund(user.getId(), request.getOrderId(), request.getRefundReason());
            return Result.success("退款申请已提交", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/refund/cancel")
    public Result<ServiceOrder> cancelRefund(@RequestParam("orderId") Long orderId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceOrder order = serviceOrderService.cancelRefund(user.getId(), orderId);
            return Result.success("退款申请已取消", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<PageResult<ServiceOrder>> getOrderList(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @RequestParam(value = "size", required = false, defaultValue = "10") long size,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        OrderQueryRequest request = new OrderQueryRequest();
        request.setStatus(status);
        request.setCategory(category);
        request.setSortBy(sortBy);
        request.setCurrent(current);
        request.setSize(size);
        request.setKeyword(keyword);
        
        PageResult<ServiceOrder> result = serviceOrderService.getOrderPage(user.getId(), request);
        return Result.success(result);
    }
    
    @GetMapping("/stats")
    public Result<Map<String, Object>> getOrderStats() {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("all", serviceOrderService.countByStatus(user.getId(), "all"));
        stats.put("pending", serviceOrderService.countByStatus(user.getId(), "pending"));
        stats.put("confirmed", serviceOrderService.countByStatus(user.getId(), "confirmed"));
        stats.put("inProgress", serviceOrderService.countByStatus(user.getId(), "inProgress"));
        stats.put("completed", serviceOrderService.countByStatus(user.getId(), "completed"));
        stats.put("cancelled", serviceOrderService.countByStatus(user.getId(), "cancelled"));
        
        return Result.success(stats);
    }
    
    @PostMapping("/review")
    public Result<OrderReview> createReview(@Validated @RequestBody ReviewCreateRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            OrderReview review = orderReviewService.createReview(user.getId(), request);
            return Result.success("评价提交成功", review);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/review/{orderId}")
    public Result<OrderReview> getReviewByOrderId(@PathVariable("orderId") Long orderId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        OrderReview review = orderReviewService.getReviewByOrderId(user.getId(), orderId);
        return Result.success(review);
    }
}
