package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_transaction")
public class BalanceTransaction {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String transactionNo;
    
    private Long userId;
    
    private Integer type;
    
    private BigDecimal amount;
    
    private BigDecimal balanceBefore;
    
    private BigDecimal balanceAfter;
    
    private Long relatedId;
    
    private String relatedType;
    
    private String description;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
