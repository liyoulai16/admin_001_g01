package com.community.dto;

import lombok.Data;

@Data
public class AppointmentQueryRequest {
    
    private String status;
    
    private String appointmentDate;
    
    private Long current = 1L;
    
    private Long size = 10L;
}
