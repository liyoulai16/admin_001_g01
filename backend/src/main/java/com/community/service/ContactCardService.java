package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.ContactCard;
import com.community.mapper.ContactCardMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactCardService {
    
    @Resource
    private ContactCardMapper contactCardMapper;
    
    public List<ContactCard> getAllContactCards() {
        return contactCardMapper.selectList(
            new LambdaQueryWrapper<ContactCard>()
                .eq(ContactCard::getStatus, 1)
                .eq(ContactCard::getDeleted, 0)
                .orderByAsc(ContactCard::getSortOrder)
        );
    }
    
    public ContactCard getContactCardById(Long id) {
        return contactCardMapper.selectById(id);
    }
    
    public Page<ContactCard> getContactCardPage(int pageNum, int pageSize) {
        Page<ContactCard> page = new Page<>(pageNum, pageSize);
        return contactCardMapper.selectPage(page,
            new LambdaQueryWrapper<ContactCard>()
                .eq(ContactCard::getDeleted, 0)
                .orderByAsc(ContactCard::getSortOrder)
        );
    }
    
    public boolean createContactCard(ContactCard contactCard) {
        if (!StringUtils.hasText(contactCard.getTitle())) {
            return false;
        }
        if (contactCard.getSortOrder() == null) {
            contactCard.setSortOrder(0);
        }
        if (contactCard.getStatus() == null) {
            contactCard.setStatus(1);
        }
        contactCard.setDeleted(0);
        contactCard.setCreateTime(LocalDateTime.now());
        contactCard.setUpdateTime(LocalDateTime.now());
        return contactCardMapper.insert(contactCard) > 0;
    }
    
    public boolean updateContactCard(ContactCard contactCard) {
        ContactCard existing = contactCardMapper.selectById(contactCard.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        if (contactCard.getIcon() != null) {
            existing.setIcon(contactCard.getIcon());
        }
        if (StringUtils.hasText(contactCard.getTitle())) {
            existing.setTitle(contactCard.getTitle());
        }
        if (contactCard.getDetail() != null) {
            existing.setDetail(contactCard.getDetail());
        }
        if (contactCard.getHint() != null) {
            existing.setHint(contactCard.getHint());
        }
        if (contactCard.getSortOrder() != null) {
            existing.setSortOrder(contactCard.getSortOrder());
        }
        if (contactCard.getStatus() != null) {
            existing.setStatus(contactCard.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        return contactCardMapper.updateById(existing) > 0;
    }
    
    public boolean deleteContactCard(Long id) {
        ContactCard existing = contactCardMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return contactCardMapper.updateById(existing) > 0;
    }
    
    public boolean updateStatus(Long id, Integer status) {
        ContactCard existing = contactCardMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return contactCardMapper.updateById(existing) > 0;
    }
}
