package com.airtel.ims.controller;

import com.airtel.ims.model.*;
import com.airtel.ims.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by("fullName").ascending());
        model.addAttribute("employees", employeeService.searchEmployees(search, pageable));
        model.addAttribute("search", search);
        model.addAttribute("activePage", "employees");
        return "employees/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("formTitle", "Add New Employee");
        model.addAttribute("activePage", "employees");
        return "employees/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Employee employee, BindingResult result,
                       Model model, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("formTitle", "Save Employee");
            return "employees/form";
        }
        employeeService.save(employee);
        redirectAttrs.addFlashAttribute("successMessage", "Employee saved successfully!");
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        return employeeService.findById(id).map(emp -> {
            model.addAttribute("employee", emp);
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("formTitle", "Edit Employee");
            model.addAttribute("activePage", "employees");
            return "employees/form";
        }).orElseGet(() -> {
            redirectAttrs.addFlashAttribute("errorMessage", "Employee not found.");
            return "redirect:/employees";
        });
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        employeeService.deactivate(id);
        redirectAttrs.addFlashAttribute("successMessage", "Employee deactivated.");
        return "redirect:/employees";
    }
}

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("activePage", "departments");
        return "departments/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("formTitle", "Add Department");
        model.addAttribute("activePage", "departments");
        return "departments/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Department department, RedirectAttributes redirectAttrs) {
        departmentService.save(department);
        redirectAttrs.addFlashAttribute("successMessage", "Department saved!");
        return "redirect:/departments";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        return departmentService.findById(id).map(dept -> {
            model.addAttribute("department", dept);
            model.addAttribute("formTitle", "Edit Department");
            model.addAttribute("activePage", "departments");
            return "departments/form";
        }).orElseGet(() -> {
            redirectAttrs.addFlashAttribute("errorMessage", "Department not found.");
            return "redirect:/departments";
        });
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        departmentService.delete(id);
        redirectAttrs.addFlashAttribute("successMessage", "Department deleted.");
        return "redirect:/departments";
    }
}
