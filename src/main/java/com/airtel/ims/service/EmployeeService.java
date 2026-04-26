package com.airtel.ims.service;

import com.airtel.ims.model.*;
import com.airtel.ims.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Page<Employee> searchEmployees(String search, Pageable pageable) {
        return employeeRepository.searchEmployees(search, pageable);
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deactivate(Long id) {
        employeeRepository.findById(id).ifPresent(e -> {
            e.setActive(false);
            employeeRepository.save(e);
        });
    }

    public List<Employee> findAllActive() {
        return employeeRepository.findByActiveTrue();
    }
}
