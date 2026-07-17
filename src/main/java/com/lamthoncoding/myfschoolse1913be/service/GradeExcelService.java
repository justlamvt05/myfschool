package com.lamthoncoding.myfschoolse1913be.service;

import com.lamthoncoding.myfschoolse1913be.payload.response.GradeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GradeExcelService {
    List<GradeResponse> importGrades(MultipartFile file, Long classId, Long semesterId, Long currentUserId);

    byte[] exportSubjectGrades(Long classId, Long semesterId, Long currentUserId);

    byte[] exportHomeroomGrades(Long semesterId, Long currentUserId);
}
