package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.FAQ;
import com.community.mapper.FAQMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FAQService {
    
    @Resource
    private FAQMapper faqMapper;
    
    public List<FAQ> getAllFAQs() {
        return faqMapper.selectList(
            new LambdaQueryWrapper<FAQ>()
                .eq(FAQ::getStatus, 1)
                .eq(FAQ::getDeleted, 0)
                .orderByAsc(FAQ::getSortOrder)
        );
    }
    
    public FAQ getFAQById(Long id) {
        return faqMapper.selectById(id);
    }
    
    public Page<FAQ> getFAQPage(int pageNum, int pageSize) {
        Page<FAQ> page = new Page<>(pageNum, pageSize);
        return faqMapper.selectPage(page,
            new LambdaQueryWrapper<FAQ>()
                .eq(FAQ::getDeleted, 0)
                .orderByAsc(FAQ::getSortOrder)
        );
    }
    
    public boolean createFAQ(FAQ faq) {
        if (!StringUtils.hasText(faq.getQuestion())) {
            return false;
        }
        if (!StringUtils.hasText(faq.getAnswer())) {
            return false;
        }
        if (faq.getSortOrder() == null) {
            faq.setSortOrder(0);
        }
        if (faq.getStatus() == null) {
            faq.setStatus(1);
        }
        faq.setDeleted(0);
        faq.setCreateTime(LocalDateTime.now());
        faq.setUpdateTime(LocalDateTime.now());
        return faqMapper.insert(faq) > 0;
    }
    
    public boolean updateFAQ(FAQ faq) {
        FAQ existing = faqMapper.selectById(faq.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (StringUtils.hasText(faq.getQuestion())) {
            existing.setQuestion(faq.getQuestion());
        }
        if (StringUtils.hasText(faq.getAnswer())) {
            existing.setAnswer(faq.getAnswer());
        }
        if (faq.getSortOrder() != null) {
            existing.setSortOrder(faq.getSortOrder());
        }
        if (faq.getStatus() != null) {
            existing.setStatus(faq.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return faqMapper.updateById(existing) > 0;
    }
    
    public boolean deleteFAQ(Long id) {
        FAQ existing = faqMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return faqMapper.updateById(existing) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        FAQ existing = faqMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return faqMapper.updateById(existing) > 0;
    }
}
