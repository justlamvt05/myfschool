package com.lamthoncoding.myfschoolse1913be.service.impl;

import com.lamthoncoding.myfschoolse1913be.entity.*;
import com.lamthoncoding.myfschoolse1913be.exception.handlers.AccessDeniedException;
import com.lamthoncoding.myfschoolse1913be.exception.handlers.EntityNotFound;
import com.lamthoncoding.myfschoolse1913be.exception.handlers.InvalidInputException;
import com.lamthoncoding.myfschoolse1913be.payload.response.GradeResponse;
import com.lamthoncoding.myfschoolse1913be.repository.*;
import com.lamthoncoding.myfschoolse1913be.service.GradeExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeExcelServiceImpl implements GradeExcelService {

    private final GradeRepository gradeRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassRoomRepository classRoomRepository;
    private final StudentClassRoomRepository studentClassRoomRepository;

    @Override
    @Transactional
    public List<GradeResponse> importGrades(MultipartFile file, Long classId, Long semesterId, Long currentUserId) {
        log.info("Teacher {} importing grades for class {} semester {}", currentUserId, classId, semesterId);
        
        Teacher teacher = getTeacher(currentUserId);
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy lớp học"));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy học kỳ"));

        List<Schedule> schedules = scheduleRepository.findByTeacherIdAndClassRoomIdAndSubjectId(
                teacher.getId(), classId, teacher.getSubject().getId());
        if (schedules.isEmpty()) {
            throw new AccessDeniedException("Bạn không được phân công dạy lớp này");
        }
        
        Subject subject = teacher.getSubject();
        List<GradeResponse> responses = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            if (rows.hasNext()) {
                rows.next(); // Skip header
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                
                Cell codeCell = currentRow.getCell(1); // Column 1: Mã học sinh
                if (codeCell == null || codeCell.getCellType() == CellType.BLANK) {
                    continue;
                }
                
                String studentCode = getCellStringValue(codeCell);
                if (studentCode.isEmpty()) continue;
                
                Optional<Student> studentOpt = studentRepository.findByStudentCode(studentCode);
                if (studentOpt.isEmpty()) {
                    continue;
                }
                Student student = studentOpt.get();

                boolean isInClass = student.getClassRoom() != null && student.getClassRoom().getId().equals(classId);
                if (!isInClass) {
                    isInClass = studentClassRoomRepository.findByStudentIdAndClassRoom_SchoolYear(student.getId(), classRoom.getSchoolYear())
                            .map(sc -> sc.getClassRoom().getId().equals(classId))
                            .orElse(false);
                }
                
                if (!isInClass) {
                    continue; 
                }

                Double oralScore = getNumericValue(currentRow.getCell(3));
                Double score15Min = getNumericValue(currentRow.getCell(4));
                Double score1Period = getNumericValue(currentRow.getCell(5));
                Double finalExam = getNumericValue(currentRow.getCell(6));

                Double averageScore = calculateAverage(oralScore, score15Min, score1Period, finalExam);

                Grade grade = gradeRepository.findByStudentIdAndSubjectIdAndSemesterId(student.getId(), subject.getId(), semesterId)
                        .orElse(Grade.builder()
                                .student(student)
                                .subject(subject)
                                .semester(semester)
                                .build());

                if (oralScore != null) grade.setOralScore(oralScore);
                if (score15Min != null) grade.setScore15Min(score15Min);
                if (score1Period != null) grade.setScore1Period(score1Period);
                if (finalExam != null) grade.setFinalExam(finalExam);
                
                grade.setAverageScore(averageScore);

                Grade saved = gradeRepository.save(grade);
                responses.add(toGradeResponse(saved));
            }

        } catch (Exception e) {
            log.error("Error importing grades: ", e);
            throw new InvalidInputException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return responses;
    }

    @Override
    public byte[] exportSubjectGrades(Long classId, Long semesterId, Long currentUserId) {
        log.info("Teacher {} exporting grades for class {} semester {}", currentUserId, classId, semesterId);
        
        Teacher teacher = getTeacher(currentUserId);
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy lớp học"));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy học kỳ"));

        List<Schedule> schedules = scheduleRepository.findByTeacherIdAndClassRoomIdAndSubjectId(
                teacher.getId(), classId, teacher.getSubject().getId());
        if (schedules.isEmpty()) {
            throw new AccessDeniedException("Bạn không được phân công dạy lớp này");
        }

        List<Student> students = getStudentsInClass(classId, classRoom.getSchoolYear());
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Điểm môn " + teacher.getSubject().getSubjectName());
            
            // Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Mã học sinh", "Họ tên", "Điểm miệng", "15 phút", "1 tiết", "Cuối kỳ", "Trung bình"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(student.getStudentCode());
                row.createCell(2).setCellValue(student.getUser().getFullName());
                
                Optional<Grade> gradeOpt = gradeRepository.findByStudentIdAndSubjectIdAndSemesterId(
                        student.getId(), teacher.getSubject().getId(), semesterId);
                        
                if (gradeOpt.isPresent()) {
                    Grade g = gradeOpt.get();
                    if (g.getOralScore() != null) row.createCell(3).setCellValue(g.getOralScore());
                    if (g.getScore15Min() != null) row.createCell(4).setCellValue(g.getScore15Min());
                    if (g.getScore1Period() != null) row.createCell(5).setCellValue(g.getScore1Period());
                    if (g.getFinalExam() != null) row.createCell(6).setCellValue(g.getFinalExam());
                    if (g.getAverageScore() != null) row.createCell(7).setCellValue(g.getAverageScore());
                }
            }
            
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting subject grades: ", e);
            throw new RuntimeException("Lỗi khi tạo file Excel");
        }
    }

    @Override
    public byte[] exportHomeroomGrades(Long semesterId, Long currentUserId) {
        log.info("Teacher {} exporting homeroom grades for semester {}", currentUserId, semesterId);
        
        Teacher teacher = getTeacher(currentUserId);
        if (teacher.getHomeroomClass() == null) {
            throw new AccessDeniedException("Bạn không phải là giáo viên chủ nhiệm của lớp nào");
        }
        
        ClassRoom classRoom = teacher.getHomeroomClass();
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy học kỳ"));

        List<Student> students = getStudentsInClass(classRoom.getId(), classRoom.getSchoolYear());
        List<Grade> allGrades = gradeRepository.findByStudentClassRoomIdAndSemesterId(classRoom.getId(), semesterId);
        
        // Map: StudentId -> Map<SubjectName, AverageScore>
        Map<Long, Map<String, Double>> studentGrades = new HashMap<>();
        Set<String> subjectNames = new TreeSet<>();
        
        for (Grade g : allGrades) {
            studentGrades.computeIfAbsent(g.getStudent().getId(), k -> new HashMap<>())
                    .put(g.getSubject().getSubjectName(), g.getAverageScore());
            subjectNames.add(g.getSubject().getSubjectName());
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Bảng điểm lớp " + classRoom.getClassName());
            
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("STT");
            headerRow.createCell(1).setCellValue("Mã học sinh");
            headerRow.createCell(2).setCellValue("Họ tên");
            
            List<String> subjects = new ArrayList<>(subjectNames);
            for (int i = 0; i < subjects.size(); i++) {
                headerRow.createCell(3 + i).setCellValue(subjects.get(i));
            }
            
            int rowIdx = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(student.getStudentCode());
                row.createCell(2).setCellValue(student.getUser().getFullName());
                
                Map<String, Double> grades = studentGrades.getOrDefault(student.getId(), new HashMap<>());
                for (int i = 0; i < subjects.size(); i++) {
                    Double score = grades.get(subjects.get(i));
                    if (score != null) {
                        row.createCell(3 + i).setCellValue(score);
                    }
                }
            }
            
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting homeroom grades: ", e);
            throw new RuntimeException("Lỗi khi tạo file Excel");
        }
    }

    private Teacher getTeacher(Long userId) {
        return teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFound("Không tìm thấy giáo viên"));
    }
    
    private List<Student> getStudentsInClass(Long classId, String schoolYear) {
        List<Student> students = studentClassRoomRepository.findByClassRoomId(classId)
                .stream()
                .map(StudentClassRoom::getStudent)
                .collect(Collectors.toList());
        if (students.isEmpty()) {
            students = studentRepository.findByClassRoomId(classId);
        }
        return students;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return "";
    }

    private Double getNumericValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim().replace(",", "."));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private Double calculateAverage(Double oral, Double min15, Double period1, Double finalExam) {
        double total = 0;
        int count = 0;
        if (oral != null) { total += oral; count += 1; }
        if (min15 != null) { total += min15; count += 1; }
        if (period1 != null) { total += period1 * 2; count += 2; }
        if (finalExam != null) { total += finalExam * 3; count += 3; }
        if (count == 0) return null;
        return Math.round((total / count) * 100.0) / 100.0;
    }

    private GradeResponse toGradeResponse(Grade grade) {
        return GradeResponse.builder()
                .subjectName(grade.getSubject() != null ? grade.getSubject().getSubjectName() : null)
                .oralScore(grade.getOralScore())
                .score15Min(grade.getScore15Min())
                .score1Period(grade.getScore1Period())
                .finalExam(grade.getFinalExam())
                .averageScore(grade.getAverageScore())
                .semesterName(grade.getSemester() != null ? grade.getSemester().getSemesterName() : null)
                .build();
    }
}
