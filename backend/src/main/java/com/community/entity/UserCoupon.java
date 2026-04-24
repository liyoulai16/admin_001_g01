package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
public class UserCoupon {
    
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_USED = "used";
    public static final String STATUS_EXPIRED = "expired";
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long couponId;
    
    private String name;
    
    private String type;
    
    private java.math.BigDecimal discountAmount;
    
    private java.math.BigDecimal discountPercent;
    
    private java.math.BigDecimal maxDiscount;
    
    private java.math.BigDecimal minSpend;
    
    private LocalDate validFrom;
    
    private LocalDate validTo;
    
    private String category;
    
    private String description;
    
    private String status;
    
    private LocalDateTime obtainTime;
    
    private LocalDateTime useTime;
    
    private Long orderId;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
