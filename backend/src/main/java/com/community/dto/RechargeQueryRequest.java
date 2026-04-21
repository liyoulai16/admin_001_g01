package com.community.dto;

import lombok.Data;

@Data
public class RechargeQueryRequest {
    
    private Integer payStatus;
    
    private Long current = 1L;
    
    private Long size = 10L;
}
