package com.lamthoncoding.myfschoolse1913be.payload.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassGradeResponse {

    private Long studentId;

    private String studentName;

    private String studentCode;

    private String subjectName;

    private Double oralScore;

    private Double score15Min;

    private Double score1Period;

    private Double finalExam;

    private Double averageScore;

    private String semesterName;
}
