package com.community.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeCreateRequest {
    
    @NotNull(message = "充值金额不能为空")
    private BigDecimal amount;
    
    private Integer payMethod;
}
