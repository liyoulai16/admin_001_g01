package com.community.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    
    private List<T> records;
    private long total;
    private long current;
    private long size;
    private long pages;
    
    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        result.setPages(total % size == 0 ? total / size : total / size + 1);
        return result;
    }
}
