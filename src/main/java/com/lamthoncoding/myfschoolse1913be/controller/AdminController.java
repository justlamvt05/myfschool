package com.lamthoncoding.myfschoolse1913be.controller;

import com.lamthoncoding.myfschoolse1913be.contraints.ApplicationStatus;
import com.lamthoncoding.myfschoolse1913be.payload.request.ReviewRequest;
import com.lamthoncoding.myfschoolse1913be.payload.request.UserCreateRequest;
import com.lamthoncoding.myfschoolse1913be.payload.request.UserUpdateRequest;
import com.lamthoncoding.myfschoolse1913be.payload.response.ApiResponse;
import com.lamthoncoding.myfschoolse1913be.security.service.UserDetailsImpl;
import com.lamthoncoding.myfschoolse1913be.service.ApplicationService;
import com.lamthoncoding.myfschoolse1913be.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ApplicationService applicationService;

    // ==================== USER APIs ====================

    @GetMapping("/users")
    public ApiResponse<?> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(userService.getUsers(name, phone, pageable));
    }

    @PostMapping("/users")
    public ApiResponse<?> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(userService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<?> deleteUser(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ApiResponse.success("Xóa người dùng thành công");
    }

    @PostMapping("/users/import")
    public ApiResponse<?> importUsers(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(userService.importUsers(file));
    }

    @GetMapping("/users/export")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        byte[] excelContent = userService.exportUsers(name, phone);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
    }

    // ==================== APPLICATION APIs ====================

    @GetMapping("/applications")
    public ApiResponse<?> getAllApplications(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(applicationService.getAllApplications(name, phone, status, pageable));
    }

    @GetMapping("/applications/{id}")
    public ApiResponse<?> getApplicationById(@PathVariable Long id) {
        return ApiResponse.success(applicationService.getApplicationById(id));
    }

    @PutMapping("/applications/{id}/approve")
    public ApiResponse<?> approveApplication(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(
                applicationService.approveApplication(id, request, userDetails.getUserId()));
    }

    @PutMapping("/applications/{id}/reject")
    public ApiResponse<?> rejectApplication(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(
                applicationService.rejectApplication(id, request, userDetails.getUserId()));
    }
}
