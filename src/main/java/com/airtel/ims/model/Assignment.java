package com.airtel.ims.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @NotNull(message = "Asset is required")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    private Employee employee;

    @Column(name = "issued_by", nullable = false, length = 150)
    @NotBlank(message = "Issued by is required")
    private String issuedBy;

    @Column(name = "issue_date", nullable = false)
    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_on_issue", nullable = false)
    @NotNull(message = "Condition on issue is required")
    private Asset.ConditionStatus conditionOnIssue;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_on_return")
    private Asset.ConditionStatus conditionOnReturn;

    @Column(length = 255)
    private String purpose;

    @Column(name = "return_notes", columnDefinition = "TEXT")
    private String returnNotes;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum AssignmentStatus {
        ACTIVE, RETURNED, OVERDUE
    }
}
