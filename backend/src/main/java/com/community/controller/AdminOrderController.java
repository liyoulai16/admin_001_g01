package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.ServiceOrder;
import com.community.service.ServiceOrderService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminOrderController {
    
    @Resource
    private ServiceOrderService serviceOrderService;
    
    @GetMapping("/orders")
    public Result<Page<ServiceOrder>> getOrderList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        
        Page<ServiceOrder> page = serviceOrderService.getOrderPageForAdmin(pageNum, pageSize, keyword, categoryId, status);
        return Result.success(page);
    }
    
    @GetMapping("/orders/{id}")
    public Result<ServiceOrder> getOrderDetail(@PathVariable Long id) {
        ServiceOrder order = serviceOrderService.getOrderByIdForAdmin(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }
    
    @GetMapping("/orders/stats")
    public Result<Map<String, Long>> getOrderStats() {
        Map<String, Long> stats = serviceOrderService.getOrderStatsForAdmin();
        return Result.success(stats);
    }
    
    @PutMapping("/orders/{id}/status")
    public Result<String> updateOrderStatus(@PathVariable Long id, 
            @RequestParam String status,
            @RequestParam(required = false) String reason) {
        
        boolean success = serviceOrderService.updateOrderStatusByAdmin(id, status, reason);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }
    
    @PostMapping("/orders/{id}/refund/approve")
    public Result<ServiceOrder> approveRefund(@PathVariable Long id) {
        try {
            ServiceOrder order = serviceOrderService.processRefund(id, true, null);
            return Result.success("退款已处理", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/orders/{id}/refund/reject")
    public Result<ServiceOrder> rejectRefund(@PathVariable Long id, 
            @RequestParam(required = false) String rejectReason) {
        try {
            ServiceOrder order = serviceOrderService.processRefund(id, false, rejectReason);
            return Result.success("退款已拒绝", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/orders/{id}")
    public Result<String> deleteOrder(@PathVariable Long id) {
        boolean success = serviceOrderService.deleteOrderByAdmin(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/orders/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id,
            @RequestParam(required = false) String cancelReason) {
        try {
            ServiceOrder order = serviceOrderService.getOrderByIdForAdmin(id);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            boolean success = serviceOrderService.updateOrderStatusByAdmin(id, "cancelled", cancelReason);
            if (success) {
                return Result.success("订单已取消");
            } else {
                return Result.error("取消失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/orders/{id}/complete")
    public Result<String> completeOrder(@PathVariable Long id) {
        try {
            ServiceOrder order = serviceOrderService.getOrderByIdForAdmin(id);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            boolean success = serviceOrderService.updateOrderStatusByAdmin(id, "completed", null);
            if (success) {
                return Result.success("订单已完成");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
