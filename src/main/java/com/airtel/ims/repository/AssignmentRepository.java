package com.airtel.ims.repository;

import com.airtel.ims.model.Assignment;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:assetId IS NULL OR a.asset.id = :assetId) AND " +
           "(:employeeId IS NULL OR a.employee.id = :employeeId)")
    Page<Assignment> searchAssignments(
            @Param("status") Assignment.AssignmentStatus status,
            @Param("assetId") Long assetId,
            @Param("employeeId") Long employeeId,
            Pageable pageable);

    @Query("SELECT a FROM Assignment a WHERE a.status = 'ACTIVE' AND a.expectedReturnDate < :today")
    List<Assignment> findOverdueAssignments(@Param("today") LocalDate today);

    long countByStatus(Assignment.AssignmentStatus status);
}
