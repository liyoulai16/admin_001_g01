package com.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCreateRequest {
    
    @NotNull(message = "服务ID不能为空")
    private Long serviceId;
    
    @NotNull(message = "数量不能为空")
    private Integer quantity;
    
    private String appointmentTime;
    
    private String appointmentDate;
    
    private String appointmentStartTime;
    
    private String appointmentEndTime;
    
    @NotBlank(message = "服务地址不能为空")
    private String serviceAddress;
    
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;
    
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    
    private String remark;
    
    private Integer payMethod;
    
    private BigDecimal extraFee;
    
    private BigDecimal discountAmount;
}
