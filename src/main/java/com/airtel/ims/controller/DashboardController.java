package com.airtel.ims.controller;

import com.airtel.ims.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AssetService assetService;
    private final AssignmentService assignmentService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("assetStats", assetService.getDashboardStats());
        model.addAttribute("assignmentStats", assignmentService.getAssignmentStats());
        model.addAttribute("overdueList", assignmentService.findOverdue());
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
