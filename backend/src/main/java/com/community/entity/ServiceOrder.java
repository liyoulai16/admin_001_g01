package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("service_order")
public class ServiceOrder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long serviceId;
    
    private Long categoryId;
    
    private String serviceName;
    
    private String categoryName;
    
    private String serviceIcon;
    
    private String serviceImage;
    
    private String serviceDescription;
    
    private BigDecimal unitPrice;
    
    private Integer quantity;
    
    private BigDecimal totalAmount;
    
    private BigDecimal extraFee;
    
    private BigDecimal discountAmount;
    
    private BigDecimal payAmount;
    
    private String status;
    
    private Integer payMethod;
    
    private Integer payStatus;
    
    private LocalDateTime payTime;
    
    private String transactionId;
    
    private String appointmentTime;
    
    private LocalDate appointmentDate;
    
    private LocalTime appointmentStartTime;
    
    private LocalTime appointmentEndTime;
    
    private String serviceAddress;
    
    private String contactName;
    
    private String contactPhone;
    
    private Integer isReviewed;
    
    private Long reviewId;
    
    private String cancelReason;
    
    private LocalDateTime cancelTime;
    
    private String remark;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
