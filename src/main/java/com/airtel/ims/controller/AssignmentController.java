package com.airtel.ims.controller;

import com.airtel.ims.model.*;
import com.airtel.ims.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssetService assetService;
    private final EmployeeService employeeService;

    @GetMapping
    public String listAssignments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Assignment.AssignmentStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try { statusEnum = Assignment.AssignmentStatus.valueOf(status); } catch (Exception ignored) {}
        }

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());
        Page<Assignment> assignmentPage = assignmentService.searchAssignments(statusEnum, null, null, pageable);

        model.addAttribute("assignments", assignmentPage);
        model.addAttribute("assignmentStatuses", Assignment.AssignmentStatus.values());
        model.addAttribute("filterStatus", status);
        model.addAttribute("activePage", "assignments");
        return "assignments/list";
    }

    @GetMapping("/issue")
    public String issueForm(Model model) {
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("availableAssets", assetService.findAvailableAssets());
        model.addAttribute("employees", employeeService.findAllActive());
        model.addAttribute("conditionStatuses", Asset.ConditionStatus.values());
        model.addAttribute("activePage", "assignments");
        return "assignments/issue";
    }

    @PostMapping("/issue")
    public String issueAsset(
            @RequestParam Long assetId,
            @RequestParam Long employeeId,
            @RequestParam String issuedBy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expectedReturnDate,
            @RequestParam String conditionOnIssue,
            @RequestParam(required = false) String purpose,
            RedirectAttributes redirectAttrs) {

        Asset asset = assetService.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        com.airtel.ims.model.Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Assignment assignment = Assignment.builder()
                .asset(asset)
                .employee(employee)
                .issuedBy(issuedBy)
                .issueDate(issueDate)
                .expectedReturnDate(expectedReturnDate)
                .conditionOnIssue(Asset.ConditionStatus.valueOf(conditionOnIssue))
                .purpose(purpose)
                .build();

        assignmentService.issueAsset(assignment);
        redirectAttrs.addFlashAttribute("successMessage", "Asset issued successfully!");
        return "redirect:/assignments";
    }

    @GetMapping("/{id}/return")
    public String returnForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        return assignmentService.findById(id).map(assignment -> {
            model.addAttribute("assignment", assignment);
            model.addAttribute("conditionStatuses", Asset.ConditionStatus.values());
            model.addAttribute("activePage", "assignments");
            return "assignments/return";
        }).orElseGet(() -> {
            redirectAttrs.addFlashAttribute("errorMessage", "Assignment not found.");
            return "redirect:/assignments";
        });
    }

    @PostMapping("/{id}/return")
    public String processReturn(
            @PathVariable Long id,
            @RequestParam String conditionOnReturn,
            @RequestParam(required = false) String returnNotes,
            @RequestParam String returnedBy,
            RedirectAttributes redirectAttrs) {

        assignmentService.returnAsset(id, Asset.ConditionStatus.valueOf(conditionOnReturn),
                returnNotes, returnedBy);
        redirectAttrs.addFlashAttribute("successMessage", "Asset returned successfully!");
        return "redirect:/assignments";
    }
}
