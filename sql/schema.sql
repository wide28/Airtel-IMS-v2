-- Airtel Inventory Management System
--  Schema

CREATE DATABASE IF NOT EXISTS airtel_ims;
USE airtel_ims;

-- Departments
CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Assets (Devices)
CREATE TABLE assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_tag VARCHAR(50) UNIQUE NOT NULL,
    device_type ENUM('LAPTOP','DESKTOP','MOBILE_PHONE','TABLET','OTHER') NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    serial_number VARCHAR(100) UNIQUE NOT NULL,
    processor VARCHAR(100),
    ram_gb INT,
    storage_gb INT,
    os VARCHAR(100),
    purchase_date DATE,
    warranty_expiry DATE,
    condition_status ENUM('NEW','GOOD','FAIR','POOR','DAMAGED','DECOMMISSIONED') DEFAULT 'NEW',
    status ENUM('AVAILABLE','ASSIGNED','IN_REPAIR','RETIRED') DEFAULT 'AVAILABLE',
    department_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- Employees
CREATE TABLE employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(30),
    job_title VARCHAR(100),
    department_id BIGINT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- Asset Assignments (Issue/Return log)
CREATE TABLE assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    issued_by VARCHAR(150) NOT NULL,
    issue_date DATE NOT NULL,
    expected_return_date DATE,
    actual_return_date DATE,
    condition_on_issue ENUM('NEW','GOOD','FAIR','POOR','DAMAGED') NOT NULL,
    condition_on_return ENUM('NEW','GOOD','FAIR','POOR','DAMAGED'),
    purpose VARCHAR(255),
    return_notes TEXT,
    status ENUM('ACTIVE','RETURNED','OVERDUE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (asset_id) REFERENCES assets(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Audit Log
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    description TEXT,
    performed_by VARCHAR(150) DEFAULT 'SYSTEM',
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed Departments
INSERT INTO departments (name, location) VALUES
('IT Department', 'Floor 3, HQ'),
('Finance', 'Floor 2, HQ'),
('Human Resources', 'Floor 1, HQ'),
('Sales & Marketing', 'Floor 4, HQ'),
('Operations', 'Building B'),
('Network Operations Center', 'Building A - NOC');

-- Seed Employees
INSERT INTO employees (employee_id, full_name, email, phone, job_title, department_id) VALUES
('ATL001', 'Jean Claude Habimana', 'j.habimana@airtel.rw', '+250788001001', 'IT Manager', 1),
('ATL002', 'Diane Uwase', 'd.uwase@airtel.rw', '+250788001002', 'Network Engineer', 6),
('ATL003', 'Patrick Nkurunziza', 'p.nkurunziza@airtel.rw', '+250788001003', 'Financial Analyst', 2),
('ATL004', 'Alice Mukamana', 'a.mukamana@airtel.rw', '+250788001004', 'HR Officer', 3),
('ATL005', 'Eric Bizimana', 'e.bizimana@airtel.rw', '+250788001005', 'Sales Executive', 4);

-- Seed Assets
INSERT INTO assets (asset_tag, device_type, brand, model, serial_number, processor, ram_gb, storage_gb, os, purchase_date, warranty_expiry, condition_status, status, department_id) VALUES
('ATL-LT-001', 'LAPTOP', 'Dell', 'Latitude 5520', 'DL5520-RW-001', 'Intel Core i5-11th Gen', 16, 512, 'Windows 11 Pro', '2023-01-15', '2026-01-15', 'GOOD', 'ASSIGNED', 1),
('ATL-LT-002', 'LAPTOP', 'HP', 'EliteBook 840 G8', 'HP840G8-RW-002', 'Intel Core i7-11th Gen', 16, 256, 'Windows 11 Pro', '2023-03-20', '2026-03-20', 'GOOD', 'AVAILABLE', 1),
('ATL-DT-001', 'DESKTOP', 'Lenovo', 'ThinkCentre M75q', 'LN75Q-RW-001', 'AMD Ryzen 5', 8, 256, 'Windows 10 Pro', '2022-06-10', '2025-06-10', 'FAIR', 'ASSIGNED', 2),
('ATL-MP-001', 'MOBILE_PHONE', 'Samsung', 'Galaxy S22', 'SAM-S22-RW-001', NULL, 8, 128, 'Android 13', '2022-11-01', '2024-11-01', 'GOOD', 'ASSIGNED', 4),
('ATL-LT-003', 'LAPTOP', 'Apple', 'MacBook Pro 14"', 'APL-MBP14-001', 'Apple M2 Pro', 32, 1000, 'macOS Ventura', '2023-07-05', '2026-07-05', 'NEW', 'AVAILABLE', 1);
