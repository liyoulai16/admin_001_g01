package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {
    
    public static final String TYPE_FULL_REDUCTION = "full_reduction";
    public static final String TYPE_DISCOUNT = "discount";
    
    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_EXPIRED = 2;
    public static final int STATUS_DISABLED = 3;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String type;
    
    private BigDecimal discountAmount;
    
    private BigDecimal discountPercent;
    
    private BigDecimal maxDiscount;
    
    private BigDecimal minSpend;
    
    private Integer totalQuantity;
    
    private Integer claimedQuantity;
    
    private Integer perUserLimit;
    
    private LocalDate validFrom;
    
    private LocalDate validTo;
    
    private String category;
    
    private String description;
    
    private Integer sortOrder;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
