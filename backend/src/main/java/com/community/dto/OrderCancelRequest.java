package com.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCancelRequest {
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;
}
