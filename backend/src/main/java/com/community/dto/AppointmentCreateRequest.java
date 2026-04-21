package com.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentCreateRequest {
    
    @NotNull(message = "服务ID不能为空")
    private Long serviceId;
    
    @NotBlank(message = "预约日期不能为空")
    private String appointmentDate;
    
    @NotBlank(message = "预约开始时间不能为空")
    private String appointmentStartTime;
    
    @NotBlank(message = "预约结束时间不能为空")
    private String appointmentEndTime;
    
    @NotBlank(message = "服务地址不能为空")
    private String serviceAddress;
    
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;
    
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    
    private Long orderId;
    
    private String remark;
}
