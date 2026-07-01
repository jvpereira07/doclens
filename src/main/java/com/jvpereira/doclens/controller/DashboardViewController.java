package com.jvpereira.doclens.controller;

import com.jvpereira.doclens.service.ExtractionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final ExtractionRequestService extractionRequestService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAllAttributes(extractionRequestService.getDashboardStats());
        return "dashboard/index";
    }
}
