# Online Course Enrollment System

A fully functional, enterprise-grade web application built with **JSF (JavaServer Faces)**, **Hibernate ORM**, and **MySQL**, designed to run on **WildFly Application Server**.

## 📋 Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Building the Project](#building-the-project)
- [Deployment on WildFly](#deployment-on-wildfly)
- [Usage Guide](#usage-guide)
- [Entity Relationships](#entity-relationships)
- [Navigation Flow](#navigation-flow)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

This system allows students to:
- Browse available courses
- Register for courses
- Manage enrollments
- Process payments
- View enrollment and payment history

The application follows enterprise-level best practices with clean architecture, separation of concerns, and proper MVC pattern implementation.

## 🛠 Technology Stack

- **Frontend**: JSF 2.3 (JavaServer Faces)
- **Backend**: Java 11
- **ORM**: Hibernate 5.6
- **Database**: MySQL 8+
- **Application Server**: WildFly
- **Build Tool**: Maven
- **Validation**: Bean Validation API

## ✨ Features

### Core Functionality
- ✅ Student registration and authentication
- ✅ Course browsing and search
- ✅ Course enrollment management
- ✅ Payment processing
- ✅ Enrollment cancellation
- ✅ Dashboard with enrollment and payment history

### Technical Features
- ✅ Full CRUD operations for all entities
- ✅ Hibernate lazy loading
- ✅ Cascade operations for related entities
- ✅ JSF navigation rules
- ✅ Form validation (email, password strength, dates, numeric values)
- ✅ JSF event handling (ActionListener, ValueChangeListener, ActionEvent)
- ✅ Auto-generated database schema via Hibernate
- ✅ Transaction management
- ✅ Exception handling with user-friendly messages

## 📁 Project Structure

```
src/
 ├── main/
 │    ├── java/
 │    │    └── com.example.onlinecourse/
 │    │         ├── entity/          # Hibernate entities
 │    │         │    ├── Student.java
 │    │         │    ├── Course.java
 │    │         │    ├── Enrollment.java
 │    │         │    └── Payment.java
 │    │         ├── dao/             # Data Access Object layer
 │    │         │    ├── StudentDAO.java
 │    │         │    ├── CourseDAO.java
 │    │         │    ├── EnrollmentDAO.java
 │    │         │    └── PaymentDAO.java
 │    │         ├── service/          # Business logic layer
 │    │         │    ├── StudentService.java
 │    │         │    ├── CourseService.java
 │    │         │    └── EnrollmentService.java
 │    │         ├── bean/            # JSF managed beans
 │    │         │    ├── StudentBean.java
 │    │         │    ├── CourseBean.java
 │    │         │    ├── EnrollmentBean.java
 │    │         │    └── PaymentBean.java
 │    │         └── util/            # Utility classes
 │    │              ├── HibernateUtil.java
 │    │              └── DBConnection.java
 │    ├── resources/
 │    │    ├── META-INF/
 │    │    │    └── persistence.xml
 │    │    └── hibernate.cfg.xml
 │    └── webapp/
 │         ├── WEB-INF/
 │         │    ├── faces-config.xml
 │         │    └── web.xml
 │         ├── pages/
 │         │    ├── home.xhtml
 │         │    ├── courseList.xhtml
 │         │    ├── courseDetails.xhtml
 │         │    ├── register.xhtml
 │         │    ├── payment.xhtml
 │         │    └── dashboard.xhtml
 │         └── index.xhtml
 └── pom.xml
```

## 📋 Prerequisites

Before setting up the project, ensure you have:

1. **Java Development Kit (JDK) 11** or higher
2. **Maven 3.6+** installed and configured
3. **MySQL 8+** installed and running
4. **WildFly Application Server** (latest stable version)
5. **MySQL Workbench** (optional, for database management)

## 🗄 Database Setup

### Step 1: Create Database

1. Open MySQL command line or MySQL Workbench
2. Connect to MySQL using:
   ```bash
   mysql -u root -p Saikrishna2005
   ```

3. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS online_course_db;
   USE online_course_db;
   ```

### Step 2: Database Schema

The database schema will be **automatically created** by Hibernate when you first run the application. Hibernate is configured with `hbm2ddl.auto=update`, which will:
- Create tables if they don't exist
- Update existing tables if entity structure changes
- Preserve existing data

**Note**: The following tables will be created automatically:
- `students`
- `courses`
- `enrollments`
- `payments`

### Step 3: Verify Database Connection

The connection settings are configured in:
- `src/main/resources/hibernate.cfg.xml`
- `src/main/resources/META-INF/persistence.xml`

Default connection settings:
- **URL**: `jdbc:mysql://localhost:3306/online_course_db`
- **Username**: `root`
- **Password**: `Saikrishna2005`

If your MySQL setup differs, update these files accordingly.

## 🔨 Building the Project

### Step 1: Clone/Navigate to Project Directory

```bash
cd C:\Users\admin\Desktop\Adv-Java-DA2
```

### Step 2: Build with Maven

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile Java source files
- Package the application as a WAR file
- Output: `target/online-course-enrollment.war`

### Step 3: Verify Build

Check that the WAR file is created:
```bash
ls target/online-course-enrollment.war
```

## 🚀 Deployment on WildFly

### Step 1: Start WildFly Server

1. Navigate to your WildFly installation directory
2. Start WildFly:
   ```bash
   # On Windows
   bin\standalone.bat
   
   # On Linux/Mac
   bin/standalone.sh
   ```

3. Wait for the server to start (look for "WFLYSRV0025" message)
4. WildFly will be available at: `http://localhost:8080`

### Step 2: Deploy the Application

**Option A: Automatic Deployment (Recommended)**

1. Copy the WAR file to WildFly's `deployments` folder:
   ```bash
   copy target\online-course-enrollment.war C:\wildfly\standalone\deployments\
   ```

2. WildFly will automatically detect and deploy the application
3. Check the WildFly console for deployment success message

**Option B: Manual Deployment via Management Console**

1. Open WildFly Management Console: `http://localhost:9990`
2. Login (default: admin/admin)
3. Navigate to **Deployments** → **Add** → **Upload**
4. Select `online-course-enrollment.war`
5. Click **Finish**

**Option C: CLI Deployment**

```bash
# Connect to WildFly CLI
jboss-cli.bat --connect

# Deploy the application
deploy target/online-course-enrollment.war
```

### Step 3: Access the Application

Once deployed, access the application at:
```
http://localhost:8080/online-course-enrollment/
```

**Note**: The application name in the URL matches the WAR filename.

## 📖 Usage Guide

### Application Flow

1. **Home Page** (`/pages/home.xhtml`)
   - Login with existing credentials
   - Register as a new student
   - Browse courses without login

2. **Course List** (`/pages/courseList.xhtml`)
   - View all available courses
   - Search courses by name or instructor
   - Click "View Details" to see course information

3. **Course Details** (`/pages/courseDetails.xhtml`)
   - View complete course information
   - Enroll in the course (requires login)
   - Check availability

4. **Registration** (`/pages/register.xhtml`)
   - Fill in student information
   - Form validation ensures:
     - Valid email format
     - Strong password (8+ chars, uppercase, lowercase, number, special char)
     - Required fields are filled

5. **Payment** (`/pages/payment.xhtml`)
   - Select payment method
   - Process payment for enrolled course
   - Transaction ID is auto-generated if not provided

6. **Dashboard** (`/pages/dashboard.xhtml`)
   - View all enrollments
   - View payment history
   - Cancel active enrollments
   - Navigate to course list

### Sample Data

To test the application, you can manually insert sample courses using MySQL:

```sql
USE online_course_db;

INSERT INTO courses (course_name, description, instructor, start_date, end_date, fee, capacity, enrolled_count)
VALUES 
('Java Programming Fundamentals', 'Learn core Java concepts and OOP principles', 'Dr. John Smith', '2024-02-01', '2024-05-31', 299.99, 50, 0),
('Advanced Web Development', 'Master JSF, Hibernate, and enterprise Java development', 'Prof. Jane Doe', '2024-03-01', '2024-06-30', 399.99, 40, 0),
('Database Design and Management', 'Comprehensive course on MySQL and database optimization', 'Dr. Robert Johnson', '2024-02-15', '2024-06-15', 349.99, 45, 0);
```

## 🔗 Entity Relationships

### Database Schema

```
Student (1) ────< (N) Enrollment (N) >─── (1) Course
  │                                              │
  │                                              │
  │ (1)                                          │ (1)
  │                                              │
  └───< (N) Payment >───────────────────────────┘
```

### Relationship Details

1. **Student ↔ Course** (Many-to-Many through Enrollment)
   - A student can enroll in multiple courses
   - A course can have multiple students
   - Enrollment entity acts as join table with additional fields (status, enrollment date)

2. **Student ↔ Payment** (One-to-Many)
   - A student can make multiple payments
   - Each payment belongs to one student

3. **Course ↔ Payment** (One-to-Many)
   - A course can have multiple payments
   - Each payment is for one course

### Cascade Operations

- Deleting a student cascades to enrollments and payments
- Deleting a course cascades to enrollments and payments
- Enrollment cancellation updates course enrollment count

## 🧭 Navigation Flow

```
index.xhtml
    ↓
home.xhtml
    ├──→ register.xhtml → dashboard.xhtml
    ├──→ courseList.xhtml
    │       ↓
    │   courseDetails.xhtml
    │       ├──→ register.xhtml (if not logged in)
    │       └──→ payment.xhtml → dashboard.xhtml
    └──→ dashboard.xhtml (if logged in)
```

Navigation is handled through:
- JSF navigation rules in `faces-config.xml`
- Managed bean action methods returning navigation outcomes
- `faces-redirect=true` for clean URLs

## ⚙️ Configuration Files

### hibernate.cfg.xml
- Database connection settings
- Hibernate dialect configuration
- DDL auto-generation settings
- Entity mappings

### persistence.xml
- JPA persistence unit configuration
- Entity class declarations
- Database properties

### web.xml
- JSF servlet configuration
- Welcome file settings
- Context parameters
- Error page mappings

### faces-config.xml
- Navigation rules
- Resource bundle configuration
- Managed bean declarations (if using XML instead of annotations)

## 🐛 Troubleshooting

### Issue: Database Connection Failed

**Solution**:
1. Verify MySQL is running: `mysql -u root -p`
2. Check database exists: `SHOW DATABASES;`
3. Verify credentials in `hibernate.cfg.xml`
4. Ensure MySQL allows connections from localhost

### Issue: Application Not Deploying

**Solution**:
1. Check WildFly logs: `standalone/log/server.log`
2. Verify WAR file is in `deployments` folder
3. Ensure no port conflicts (8080, 9990)
4. Check Java version compatibility (JDK 11+)

### Issue: JSF Pages Not Loading

**Solution**:
1. Verify `web.xml` has correct JSF servlet mapping
2. Check that XHTML files are in correct location
3. Ensure managed beans are properly annotated
4. Check WildFly console for deployment errors

### Issue: Hibernate Schema Not Created

**Solution**:
1. Verify `hibernate.hbm2ddl.auto=update` in `hibernate.cfg.xml`
2. Check database connection is successful
3. Review Hibernate logs for errors
4. Ensure entity classes are properly annotated

### Issue: Validation Not Working

**Solution**:
1. Verify Bean Validation dependencies in `pom.xml`
2. Check that validation annotations are on entity fields
3. Ensure JSF form has proper validation tags
4. Check browser console for JavaScript errors

## 📝 Code Quality

This project follows:
- **SOLID Principles**: Separation of concerns, single responsibility
- **MVC Pattern**: Clear separation between Model, View, and Controller
- **DAO Pattern**: Data access abstraction
- **Service Layer**: Business logic encapsulation
- **Clean Code**: Readable, maintainable, well-documented code
- **Exception Handling**: Graceful error handling with user messages

## 🔒 Security Notes

**Important**: This is a demonstration application. For production use:

1. **Password Storage**: Implement password hashing (BCrypt, Argon2)
2. **SQL Injection**: Use parameterized queries (already implemented via Hibernate)
3. **XSS Protection**: JSF provides built-in XSS protection
4. **Session Management**: Implement proper session timeout and invalidation
5. **Input Validation**: Server-side validation is implemented; add client-side for better UX
6. **HTTPS**: Use HTTPS in production
7. **Database Credentials**: Move credentials to environment variables or secure configuration

## 📞 Support

For issues or questions:
1. Check the troubleshooting section
2. Review WildFly server logs
3. Check MySQL error logs
4. Verify all prerequisites are met

## 📄 License

This project is created for educational purposes.

---

**Built with ❤️ using JSF, Hibernate, and MySQL on WildFly**

