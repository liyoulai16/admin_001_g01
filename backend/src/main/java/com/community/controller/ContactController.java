package com.community.controller;

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
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {
    
    @Resource
    private ContactCardService contactCardService;
    
    @Resource
    private WorkingHourService workingHourService;
    
    @Resource
    private FAQService faqService;
    
    @Resource
    private FeatureService featureService;
    
    @GetMapping("/contact-cards")
    public Result<List<ContactCard>> getContactCards() {
        List<ContactCard> list = contactCardService.getAllContactCards();
        return Result.success(list);
    }
    
    @GetMapping("/working-hours")
    public Result<List<WorkingHour>> getWorkingHours() {
        List<WorkingHour> list = workingHourService.getAllWorkingHours();
        return Result.success(list);
    }
    
    @GetMapping("/faqs")
    public Result<List<FAQ>> getFAQs() {
        List<FAQ> list = faqService.getAllFAQs();
        return Result.success(list);
    }
    
    @GetMapping("/features")
    public Result<List<Feature>> getFeatures() {
        List<Feature> list = featureService.getAllFeatures();
        return Result.success(list);
    }
    
    @GetMapping("/data")
    public Result<Map<String, Object>> getContactData() {
        List<ContactCard> contactCards = contactCardService.getAllContactCards();
        List<WorkingHour> workingHours = workingHourService.getAllWorkingHours();
        List<FAQ> faqs = faqService.getAllFAQs();
        List<Feature> features = featureService.getAllFeatures();
        
        Map<String, Object> result = new HashMap<>();
        result.put("contactCards", contactCards);
        result.put("workingHours", workingHours);
        result.put("faqs", faqs);
        result.put("features", features);
        
        return Result.success(result);
    }
}
