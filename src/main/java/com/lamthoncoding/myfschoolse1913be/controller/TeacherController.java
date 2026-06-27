package com.lamthoncoding.myfschoolse1913be.controller;

import com.lamthoncoding.myfschoolse1913be.payload.request.TeacherApplicationRequest;
import com.lamthoncoding.myfschoolse1913be.payload.request.TeacherGradeRequest;
import com.lamthoncoding.myfschoolse1913be.payload.response.ApiResponse;
import com.lamthoncoding.myfschoolse1913be.security.service.UserDetailsImpl;
import com.lamthoncoding.myfschoolse1913be.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    // ==================== LỊCH DẠY ====================

    /**
     * GET /teacher/schedules
     * Lấy toàn bộ lịch dạy của giáo viên hiện tại.
     */
    @GetMapping("/schedules")
    public ApiResponse<?> getMySchedule(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.success(
                teacherService.getMySchedule(userDetails.getUserId()));
    }

    /**
     * GET /teacher/schedules/day/{day}
     * Lấy lịch dạy theo ngày trong tuần.
     */
    @GetMapping("/schedules/day/{day}")
    public ApiResponse<?> getMyScheduleByDay(
            @PathVariable DayOfWeek day,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.success(
                teacherService.getMyScheduleByDay(userDetails.getUserId(), day));
    }

    // ==================== ĐIỂM LỚP CHỦ NHIỆM ====================

    /**
     * GET /teacher/homeroom/grades
     * Xem điểm tất cả học sinh trong lớp chủ nhiệm.
     */
    @GetMapping("/homeroom/grades")
    public ApiResponse<?> getHomeroomClassGrades(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.success(
                teacherService.getHomeroomClassGrades(userDetails.getUserId()));
    }

    /**
     * GET /teacher/homeroom/grades/semester/{semesterId}
     * Xem điểm lớp chủ nhiệm theo học kỳ.
     */
    @GetMapping("/homeroom/grades/semester/{semesterId}")
    public ApiResponse<?> getHomeroomClassGradesBySemester(
            @PathVariable Long semesterId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.success(
                teacherService.getHomeroomClassGradesBySemester(
                        userDetails.getUserId(), semesterId));
    }

    // ==================== ĐƠN ====================

    /**
     * POST /teacher/applications
     * Giáo viên tạo đơn mới.
     */
    @PostMapping("/applications")
    public ApiResponse<?> createApplication(
            @Valid @RequestBody TeacherApplicationRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.create(
                teacherService.createApplication(request, userDetails.getUserId()));
    }

    /**
     * GET /teacher/applications
     * Lấy danh sách đơn của giáo viên hiện tại.
     */
    @GetMapping("/applications")
    public ApiResponse<?> getMyApplications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.success(
                teacherService.getMyApplications(userDetails.getUserId()));
    }

    /**
     * GET /teacher/applications/{id}
     * Xem chi tiết đơn của giáo viên.
     */
    @GetMapping("/applications/{id}")
    public ApiResponse<?> getMyApplicationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.success(
                teacherService.getMyApplicationById(id, userDetails.getUserId()));
    }

    // ==================== SỰ KIỆN ====================

    /**
     * GET /teacher/events
     * Xem các sự kiện (ALL + TEACHER).
     */
    @GetMapping("/events")
    public ApiResponse<?> getEvents() {

        return ApiResponse.success(
                teacherService.getEvents());
    }

    // ==================== NHẬP ĐIỂM ====================

    /**
     * POST /teacher/grades
     * Nhập điểm cho học sinh (chỉ môn GV dạy).
     */
    @PostMapping("/grades")
    public ApiResponse<?> inputGrade(
            @Valid @RequestBody TeacherGradeRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.create(
                teacherService.inputGrade(request, userDetails.getUserId()));
    }

    /**
     * PUT /teacher/grades/{id}
     * Cập nhật điểm đã nhập (chỉ môn GV dạy).
     */
    @PutMapping("/grades/{id}")
    public ApiResponse<?> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody TeacherGradeRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ApiResponse.success(
                teacherService.updateGrade(id, request, userDetails.getUserId()));
    }
}
