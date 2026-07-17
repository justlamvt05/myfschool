package com.lamthoncoding.myfschoolse1913be.repository;

import com.lamthoncoding.myfschoolse1913be.contraints.ApplicationStatus;
import com.lamthoncoding.myfschoolse1913be.contraints.ApplicationType;
import com.lamthoncoding.myfschoolse1913be.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByIdAndStudentId(Long id, Long studentId);

    List<Application> findByTeacherId(Long teacherId);

    Optional<Application> findByIdAndTeacherId(Long id, Long teacherId);


    List<Application> findByTeacherIdOrderByCreatedAtDesc (Long id);

    List<Application> findByStudentIdOrderByCreatedAtDesc (Long id);

    List<Application> findByStudentIdInOrderByCreatedAtDesc(List<Long> studentIds);

    @Query("""
    SELECT a FROM Application a
    LEFT JOIN a.student s LEFT JOIN s.user su
    LEFT JOIN a.teacher t LEFT JOIN t.user tu
    WHERE (:status IS NULL OR a.status = :status)
    AND (
        (CAST(:name AS string) IS NULL OR LOWER(su.fullName) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')) OR LOWER(tu.fullName) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
    )
    AND (
        (CAST(:phone AS string) IS NULL OR su.phone LIKE CONCAT('%', CAST(:phone AS string), '%') OR tu.phone LIKE CONCAT('%', CAST(:phone AS string), '%'))
    )
    """)
    org.springframework.data.domain.Page<Application> findApplicationsByFilters(
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("status") ApplicationStatus status,
            org.springframework.data.domain.Pageable pageable
    );
}
