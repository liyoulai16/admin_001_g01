package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.ContactMessage;
import com.community.mapper.ContactMessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ContactMessageService {
    
    @Resource
    private ContactMessageMapper contactMessageMapper;
    
    public Page<ContactMessage> getMessagePage(int pageNum, int pageSize, String type, Integer status, String keyword) {
        Page<ContactMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ContactMessage> wrapper = new LambdaQueryWrapper<ContactMessage>()
                .eq(ContactMessage::getDeleted, 0)
                .orderByDesc(ContactMessage::getCreateTime);
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(ContactMessage::getType, type);
        }
        if (status != null) {
            wrapper.eq(ContactMessage::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(ContactMessage::getName, keyword)
                    .or().like(ContactMessage::getPhone, keyword)
                    .or().like(ContactMessage::getEmail, keyword)
                    .or().like(ContactMessage::getMessage, keyword)
            );
        }
        
        return contactMessageMapper.selectPage(page, wrapper);
    }
    
    public ContactMessage getMessageById(Long id) {
        return contactMessageMapper.selectById(id);
    }
    
    public boolean createMessage(ContactMessage message) {
        if (!StringUtils.hasText(message.getName())) {
            return false;
        }
        if (!StringUtils.hasText(message.getPhone())) {
            return false;
        }
        if (!StringUtils.hasText(message.getMessage())) {
            return false;
        }
        message.setStatus(0);
        message.setDeleted(0);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        return contactMessageMapper.insert(message) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        ContactMessage existing = contactMessageMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return contactMessageMapper.updateById(existing) > 0;
    }
    
    public boolean replyMessage(Long id, String reply, Long replyBy) {
        ContactMessage existing = contactMessageMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setReply(reply);
        existing.setReplyBy(replyBy);
        existing.setReplyTime(LocalDateTime.now());
        existing.setStatus(1);
        existing.setUpdateTime(LocalDateTime.now());
        return contactMessageMapper.updateById(existing) > 0;
    }
    
    public boolean deleteMessage(Long id) {
        ContactMessage existing = contactMessageMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return contactMessageMapper.updateById(existing) > 0;
    }
}
