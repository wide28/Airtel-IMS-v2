# Airtel IMS — Inventory Management System
### End User Equipment Tracking · Airtel Rwanda

Built with: **Spring Boot 3.2** · **Thymeleaf** · **MySQL * · * CSS**

---

## Features
- **Asset Registration** — Laptops, Desktops, Mobile Phones, Tablets with full specs
- **Issue & Return Management** — Assign devices, track returns, log condition changes
- **Employee & Department Management** — Organizational hierarchy
- **Overdue Detection** — Automatic flagging of overdue assignments
- **Audit History** — Every change logged with timestamp and actor
- **Search & Filter** — By status, device type, department, free-text search
- **Pagination** — Handles large inventories
- **Print-friendly** — CSS print styles included
- **Fully Offline** — No CDN or internet required after initial font load (optional)

---

## Prerequisites
| Tool | Version |
|------|---------|
| Java (JDK) | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |

---

## Setup Instructions

### 1. Create the Database
```sql
-- Run the schema file:
mysql -u root -p < sql/schema.sql
```
This creates the `airtel_ims` database with all tables and seed data.

### 2. Configure Database Connection
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/airtel_ims?useSSL=false&serverTimezone=Africa/Kigali&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD_HERE
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

Then open your browser at: **http://localhost:8080**

### 4. Build a JAR (for deployment)
```bash
mvn clean package
java -jar target/ims-1.0.0.jar
```

---

## Project Structure
```
airtel-ims/
├── sql/
│   └── schema.sql                    ← Database schema + seed data
├── src/main/
│   ├── java/com/airtel/ims/
│   │   ├── ImsApplication.java        ← Entry point
│   │   ├── model/                     ← JPA entities
│   │   │   ├── Asset.java
│   │   │   ├── Assignment.java
│   │   │   ├── Employee.java
│   │   │   ├── Department.java
│   │   │   └── AuditLog.java
│   │   ├── repository/               ← Spring Data JPA
│   │   │   └── Repositories.java
│   │   ├── service/                  ← Business logic
│   │   │   ├── AssetService.java
│   │   │   ├── AssignmentService.java
│   │   │   ├── EmployeeService.java
│   │   │   └── DepartmentService.java
│   │   └── controller/               ← MVC Controllers
│   │       ├── DashboardController.java
│   │       ├── AssetController.java
│   │       ├── AssignmentController.java
│   │       └── EmployeeAndDeptController.java
│   └── resources/
│       ├── application.properties
│       ├── static/css/style.css       ← All styling
│       └── templates/                 ← Thymeleaf HTML
│           ├── dashboard.html
│           ├── assets/       (list, form, detail)
│           ├── assignments/  (list, issue, return)
│           ├── employees/    (list, form)
│           └── departments/  (list, form)
└── pom.xml
```

---

## Default Seed Data
- **6 Departments**: IT, Finance, HR, Sales, Operations, NOC
- **5 Employees**: Sample Airtel Rwanda staff
- **5 Assets**: Mix of laptops, desktop, mobile phone

---

## Pages & Routes
| Route | Description |
|-------|-------------|
| `GET /` | Dashboard with stats |
| `GET /assets` | Asset list with search/filter |
| `GET /assets/new` | Register new asset |
| `GET /assets/{id}/view` | Asset detail + audit log |
| `GET /assets/{id}/edit` | Edit asset |
| `GET /assignments` | Issue/Return log |
| `GET /assignments/issue` | Issue equipment form |
| `GET /assignments/{id}/return` | Process return form |
| `GET /employees` | Employee list |
| `GET /departments` | Department list |

---

## Tech Stack Decisions
- **No JavaScript** — All interactions are standard HTML form POSTs
- **No external CSS frameworks** — Custom utility-first CSS with CSS variables
- **Thymeleaf** — Server-side rendering, works fully offline
- **Spring Data JPA** — Type-safe, paginated queries
- **MySQL** — Reliable local relational database
