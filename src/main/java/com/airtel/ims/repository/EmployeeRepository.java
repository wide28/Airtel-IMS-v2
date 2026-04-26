package com.airtel.ims.repository;

import com.airtel.ims.model.Employee;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.active = true AND " +
           "(:search IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "  OR LOWER(e.employeeId) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "  OR LOWER(e.jobTitle) LIKE LOWER(CONCAT('%',:search,'%')))")
    Page<Employee> searchEmployees(@Param("search") String search, Pageable pageable);

    List<Employee> findByActiveTrue();
}
