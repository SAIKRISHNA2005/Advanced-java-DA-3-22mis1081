# Online Course Enrollment System

A fully functional, enterprise-grade web application built with **JSF (JavaServer Faces)**, **Hibernate ORM**, and **MySQL**, designed to run on **WildFly Application Server**.

## 🎯 Overview

This system allows students to:
- Browse available courses
- Register for courses
- Manage enrollments
- Process payments
- View enrollment and payment history

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


## 📋 Prerequisites

Before setting up the project, ensure you have:

1. **Java Development Kit (JDK) 11** or higher
2. **Maven 3.6+** installed and configured
3. **MySQL 8+** installed and running
4. **WildFly Application Server** (latest stable version)
5. **MySQL Workbench** (optional, for database management)




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


## 🔗 Entity Relationships

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



---

**Built with ❤️ using JSF, Hibernate, and MySQL on WildFly**

