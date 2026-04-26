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
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final DepartmentService departmentService;

    @GetMapping
    public String listAssets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Asset.AssetStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try { statusEnum = Asset.AssetStatus.valueOf(status); } catch (Exception ignored) {}
        }
        Asset.DeviceType typeEnum = null;
        if (deviceType != null && !deviceType.isEmpty()) {
            try { typeEnum = Asset.DeviceType.valueOf(deviceType); } catch (Exception ignored) {}
        }

        Pageable pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());
        Page<Asset> assetPage = assetService.searchAssets(statusEnum, typeEnum, departmentId, search, pageable);

        model.addAttribute("assets", assetPage);
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("deviceTypes", Asset.DeviceType.values());
        model.addAttribute("assetStatuses", Asset.AssetStatus.values());
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterDeviceType", deviceType);
        model.addAttribute("filterDeptId", departmentId);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "assets");
        return "assets/list";
    }

    @GetMapping("/new")
    public String newAssetForm(Model model) {
        model.addAttribute("asset", new Asset());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("deviceTypes", Asset.DeviceType.values());
        model.addAttribute("conditionStatuses", Asset.ConditionStatus.values());
        model.addAttribute("activePage", "assets");
        model.addAttribute("formTitle", "Register New Asset");
        return "assets/form";
    }

    @PostMapping("/save")
    public String saveAsset(@Valid @ModelAttribute Asset asset, BindingResult result,
                            Model model, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("deviceTypes", Asset.DeviceType.values());
            model.addAttribute("conditionStatuses", Asset.ConditionStatus.values());
            model.addAttribute("formTitle", asset.getId() == null ? "Register New Asset" : "Edit Asset");
            return "assets/form";
        }
        assetService.save(asset);
        redirectAttrs.addFlashAttribute("successMessage", "Asset saved successfully!");
        return "redirect:/assets";
    }

    @GetMapping("/{id}/edit")
    public String editAsset(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        return assetService.findById(id).map(asset -> {
            model.addAttribute("asset", asset);
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("deviceTypes", Asset.DeviceType.values());
            model.addAttribute("conditionStatuses", Asset.ConditionStatus.values());
            model.addAttribute("assetStatuses", Asset.AssetStatus.values());
            model.addAttribute("activePage", "assets");
            model.addAttribute("formTitle", "Edit Asset");
            return "assets/form";
        }).orElseGet(() -> {
            redirectAttrs.addFlashAttribute("errorMessage", "Asset not found.");
            return "redirect:/assets";
        });
    }

    @GetMapping("/{id}/view")
    public String viewAsset(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        return assetService.findById(id).map(asset -> {
            model.addAttribute("asset", asset);
            model.addAttribute("history", assetService.getAssetHistory(id));
            model.addAttribute("activePage", "assets");
            return "assets/detail";
        }).orElseGet(() -> {
            redirectAttrs.addFlashAttribute("errorMessage", "Asset not found.");
            return "redirect:/assets";
        });
    }

    @PostMapping("/{id}/delete")
    public String deleteAsset(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        assetService.delete(id);
        redirectAttrs.addFlashAttribute("successMessage", "Asset deleted.");
        return "redirect:/assets";
    }
}
