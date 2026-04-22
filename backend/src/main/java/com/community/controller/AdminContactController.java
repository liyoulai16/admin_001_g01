package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.ContactCard;
import com.community.entity.FAQ;
import com.community.entity.Feature;
import com.community.entity.WorkingHour;
import com.community.service.ContactCardService;
import com.community.service.FAQService;
import com.community.service.FeatureService;
import com.community.service.WorkingHourService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminContactController {
    
    @Resource
    private ContactCardService contactCardService;
    
    @Resource
    private WorkingHourService workingHourService;
    
    @Resource
    private FAQService faqService;
    
    @Resource
    private FeatureService featureService;
    
    // ==================== 联系方式管理 ====================
    
    @GetMapping("/contact-cards")
    public Result<Page<ContactCard>> getContactCardList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<ContactCard> page = contactCardService.getContactCardPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/contact-cards/all")
    public Result<List<ContactCard>> getAllContactCards() {
        List<ContactCard> list = contactCardService.getAllContactCards();
        return Result.success(list);
    }
    
    @GetMapping("/contact-cards/{id}")
    public Result<ContactCard> getContactCardDetail(@PathVariable Long id) {
        ContactCard item = contactCardService.getContactCardById(id);
        if (item == null) {
            return Result.error("联系方式不存在");
        }
        return Result.success(item);
    }
    
    @PostMapping("/contact-cards")
    public Result<Map<String, Object>> createContactCard(@RequestBody ContactCard item) {
        boolean success = contactCardService.createContactCard(item);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            return Result.success("创建成功", data);
        } else {
            return Result.error("创建失败，请填写必填字段");
        }
    }
    
    @PutMapping("/contact-cards/{id}")
    public Result<String> updateContactCard(@PathVariable Long id, @RequestBody ContactCard item) {
        item.setId(id);
        boolean success = contactCardService.updateContactCard(item);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/contact-cards/{id}")
    public Result<String> deleteContactCard(@PathVariable Long id) {
        boolean success = contactCardService.deleteContactCard(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/contact-cards/{id}/status")
    public Result<String> updateContactCardStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = contactCardService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
    
    // ==================== 工作时间管理 ====================
    
    @GetMapping("/working-hours")
    public Result<Page<WorkingHour>> getWorkingHourList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<WorkingHour> page = workingHourService.getWorkingHourPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/working-hours/all")
    public Result<List<WorkingHour>> getAllWorkingHours() {
        List<WorkingHour> list = workingHourService.getAllWorkingHours();
        return Result.success(list);
    }
    
    @GetMapping("/working-hours/{id}")
    public Result<WorkingHour> getWorkingHourDetail(@PathVariable Long id) {
        WorkingHour item = workingHourService.getWorkingHourById(id);
        if (item == null) {
            return Result.error("工作时间不存在");
        }
        return Result.success(item);
    }
    
    @PostMapping("/working-hours")
    public Result<Map<String, Object>> createWorkingHour(@RequestBody WorkingHour item) {
        boolean success = workingHourService.createWorkingHour(item);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            return Result.success("创建成功", data);
        } else {
            return Result.error("创建失败，请填写必填字段");
        }
    }
    
    @PutMapping("/working-hours/{id}")
    public Result<String> updateWorkingHour(@PathVariable Long id, @RequestBody WorkingHour item) {
        item.setId(id);
        boolean success = workingHourService.updateWorkingHour(item);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/working-hours/{id}")
    public Result<String> deleteWorkingHour(@PathVariable Long id) {
        boolean success = workingHourService.deleteWorkingHour(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/working-hours/{id}/status")
    public Result<String> updateWorkingHourStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = workingHourService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
    
    // ==================== 常见问题管理 ====================
    
    @GetMapping("/faqs")
    public Result<Page<FAQ>> getFAQList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<FAQ> page = faqService.getFAQPage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/faqs/all")
    public Result<List<FAQ>> getAllFAQs() {
        List<FAQ> list = faqService.getAllFAQs();
        return Result.success(list);
    }
    
    @GetMapping("/faqs/{id}")
    public Result<FAQ> getFAQDetail(@PathVariable Long id) {
        FAQ item = faqService.getFAQById(id);
        if (item == null) {
            return Result.error("常见问题不存在");
        }
        return Result.success(item);
    }
    
    @PostMapping("/faqs")
    public Result<Map<String, Object>> createFAQ(@RequestBody FAQ item) {
        boolean success = faqService.createFAQ(item);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            return Result.success("创建成功", data);
        } else {
            return Result.error("创建失败，请填写必填字段");
        }
    }
    
    @PutMapping("/faqs/{id}")
    public Result<String> updateFAQ(@PathVariable Long id, @RequestBody FAQ item) {
        item.setId(id);
        boolean success = faqService.updateFAQ(item);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/faqs/{id}")
    public Result<String> deleteFAQ(@PathVariable Long id) {
        boolean success = faqService.deleteFAQ(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/faqs/{id}/status")
    public Result<String> updateFAQStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = faqService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
    
    // ==================== 特色优势管理 ====================
    
    @GetMapping("/features")
    public Result<Page<Feature>> getFeatureList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Feature> page = featureService.getFeaturePage(pageNum, pageSize);
        return Result.success(page);
    }
    
    @GetMapping("/features/all")
    public Result<List<Feature>> getAllFeatures() {
        List<Feature> list = featureService.getAllFeatures();
        return Result.success(list);
    }
    
    @GetMapping("/features/{id}")
    public Result<Feature> getFeatureDetail(@PathVariable Long id) {
        Feature item = featureService.getFeatureById(id);
        if (item == null) {
            return Result.error("特色优势不存在");
        }
        return Result.success(item);
    }
    
    @PostMapping("/features")
    public Result<Map<String, Object>> createFeature(@RequestBody Feature item) {
        boolean success = featureService.createFeature(item);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            return Result.success("创建成功", data);
        } else {
            return Result.error("创建失败，请填写必填字段");
        }
    }
    
    @PutMapping("/features/{id}")
    public Result<String> updateFeature(@PathVariable Long id, @RequestBody Feature item) {
        item.setId(id);
        boolean success = featureService.updateFeature(item);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @DeleteMapping("/features/{id}")
    public Result<String> deleteFeature(@PathVariable Long id) {
        boolean success = featureService.deleteFeature(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    @PutMapping("/features/{id}/status")
    public Result<String> updateFeatureStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = featureService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("操作失败");
        }
    }
}
