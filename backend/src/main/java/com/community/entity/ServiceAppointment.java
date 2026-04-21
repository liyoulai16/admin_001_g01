package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("service_appointment")
public class ServiceAppointment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String appointmentNo;
    
    private Long userId;
    
    private Long orderId;
    
    private Long serviceId;
    
    private String serviceName;
    
    private LocalDate appointmentDate;
    
    private LocalTime appointmentStartTime;
    
    private LocalTime appointmentEndTime;
    
    private String appointmentTimeStr;
    
    private String serviceAddress;
    
    private String contactName;
    
    private String contactPhone;
    
    private String status;
    
    private LocalDateTime confirmTime;
    
    private LocalDateTime startTime;
    
    private LocalDateTime completeTime;
    
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
