package com.community.dto;

import lombok.Data;

@Data
public class OrderQueryRequest {
    
    private String status;
    
    private String category;
    
    private String sortBy;
    
    private Long current = 1L;
    
    private Long size = 10L;
    
    private String keyword;
}
