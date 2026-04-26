package com.airtel.ims.service;

import com.airtel.ims.model.*;
import com.airtel.ims.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssetRepository assetRepository;
    private final AuditLogRepository auditLogRepository;

    public Page<Assignment> searchAssignments(Assignment.AssignmentStatus status,
                                               Long assetId, Long employeeId, Pageable pageable) {
        return assignmentRepository.searchAssignments(status, assetId, employeeId, pageable);
    }

    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }

    public Assignment issueAsset(Assignment assignment) {
        Asset asset = assignment.getAsset();
        asset.setStatus(Asset.AssetStatus.ASSIGNED);
        assetRepository.save(asset);

        assignment.setStatus(Assignment.AssignmentStatus.ACTIVE);
        Assignment saved = assignmentRepository.save(assignment);

        auditLogRepository.save(AuditLog.builder()
                .entityType("ASSIGNMENT")
                .entityId(saved.getId())
                .action("ISSUED")
                .description("Asset " + asset.getAssetTag() + " issued to " + assignment.getEmployee().getFullName())
                .performedBy(assignment.getIssuedBy())
                .build());

        return saved;
    }

    public Assignment returnAsset(Long assignmentId, Asset.ConditionStatus returnCondition,
                                   String returnNotes, String returnedBy) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setActualReturnDate(LocalDate.now());
        assignment.setConditionOnReturn(returnCondition);
        assignment.setReturnNotes(returnNotes);
        assignment.setStatus(Assignment.AssignmentStatus.RETURNED);

        Asset asset = assignment.getAsset();
        asset.setStatus(Asset.AssetStatus.AVAILABLE);
        asset.setConditionStatus(returnCondition);
        assetRepository.save(asset);

        Assignment saved = assignmentRepository.save(assignment);

        auditLogRepository.save(AuditLog.builder()
                .entityType("ASSIGNMENT")
                .entityId(saved.getId())
                .action("RETURNED")
                .description("Asset " + asset.getAssetTag() + " returned by " + assignment.getEmployee().getFullName())
                .performedBy(returnedBy)
                .build());

        return saved;
    }

    public List<Assignment> findOverdue() {
        List<Assignment> overdue = assignmentRepository.findOverdueAssignments(LocalDate.now());
        overdue.forEach(a -> a.setStatus(Assignment.AssignmentStatus.OVERDUE));
        assignmentRepository.saveAll(overdue);
        return overdue;
    }

    public Map<String, Long> getAssignmentStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("active", assignmentRepository.countByStatus(Assignment.AssignmentStatus.ACTIVE));
        stats.put("returned", assignmentRepository.countByStatus(Assignment.AssignmentStatus.RETURNED));
        stats.put("overdue", assignmentRepository.countByStatus(Assignment.AssignmentStatus.OVERDUE));
        return stats;
    }
}
