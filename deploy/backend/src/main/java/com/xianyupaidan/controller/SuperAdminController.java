package com.xianyupaidan.controller;

import com.xianyupaidan.common.Result;
import com.xianyupaidan.dto.CreateAdminRequest;
import com.xianyupaidan.entity.Admin;
import com.xianyupaidan.service.SuperAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @GetMapping("/admins")
    public Result<List<Admin>> getAdmins() {
        return Result.ok(superAdminService.getAllAdmins());
    }

    @PostMapping("/admins")
    public Result<Admin> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return Result.ok(superAdminService.createAdmin(request));
    }

    @PutMapping("/admins/{id}")
    public Result<?> updateAdmin(@PathVariable Long id, @Valid @RequestBody CreateAdminRequest request) {
        superAdminService.updateAdmin(id, request);
        return Result.ok();
    }

    @DeleteMapping("/admins/{id}")
    public Result<?> deleteAdmin(@PathVariable Long id) {
        superAdminService.deleteAdmin(id);
        return Result.ok();
    }

    @PutMapping("/admins/{id}/status")
    public Result<?> toggleAdminStatus(@PathVariable Long id) {
        superAdminService.toggleAdminStatus(id);
        return Result.ok();
    }
}
