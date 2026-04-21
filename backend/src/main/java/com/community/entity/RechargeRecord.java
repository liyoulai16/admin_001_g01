package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recharge_record")
public class RechargeRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String rechargeNo;
    
    private Long userId;
    
    private BigDecimal amount;
    
    private BigDecimal payAmount;
    
    private BigDecimal bonusAmount;
    
    private Integer payMethod;
    
    private Integer payStatus;
    
    private LocalDateTime payTime;
    
    private String transactionId;
    
    private String remark;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
