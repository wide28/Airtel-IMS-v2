package com.airtel.ims.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @Column(name = "full_name", nullable = false, length = 150)
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Column(length = 150)
    @Email(message = "Invalid email format")
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<Assignment> assignments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
