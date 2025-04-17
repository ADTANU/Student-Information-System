package main;

import dao.*;
import entity.*;
import exception.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // Assuming DAOs are implemented and ready to use
    private static final StudentDAO studentDAO = new StudentDAOImpl();
    private static final CourseDAO courseDAO = new CourseDAOImpl();
    private static final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private static final PaymentDAO paymentDAO = new PaymentDAOImpl();
    private static final TeacherDAO teacherDAO = new TeacherDAOImpl();

    public static void main(String[] args) {
        boolean isRunning = true;

        while (isRunning) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        addStudent();
                        break;
                    case 2:
                        addCourse();
                        break;
                    case 3:
                        enrollStudentInCourse();
                        break;
                    case 4:
                        assignTeacherToCourse();
                        break;
                    case 5:
                        makePayment();
                        break;
                    case 6:
                        generateEnrollmentReport();
                        break;
                    case 7:
                        generatePaymentReport();
                        break;
                    case 8:
                        calculateCourseStatistics();
                        break;
                    case 9:
                        viewAllStudents(); // New option
                        break;
                    case 10:
                        viewAllCourses(); // New option
                        break;
                    case 11:
                        System.out.println("Exiting program...");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {
        System.out.println("==== Student Information System ====");
        System.out.println("1. Add New Student");
        System.out.println("2. Add New Course");
        System.out.println("3. Enroll Student in Course");
        System.out.println("4. Assign Teacher to Course");
        System.out.println("5. Make Payment");
        System.out.println("6. Generate Enrollment Report");
        System.out.println("7. Generate Payment Report");
        System.out.println("8. Calculate Course Statistics");
        System.out.println("9. View All Students"); // New option
        System.out.println("10. View All Courses"); // New option
        System.out.println("11. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void viewAllStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents(); // Assuming a getAllStudents method in your StudentDAO
            System.out.println("==== All Students ====");
            for (Student student : students) {
                System.out.println("ID: " + student.getStudentId() +
                        ", Name: " + student.getFirstName() + " " + student.getLastName() +
                        ", Email: " + student.getEmail() +
                        ", Phone: " + student.getPhoneNumber());
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve students: " + e.getMessage());
        }
    }

    private static void viewAllCourses() {
        try {
            List<Course> courses = courseDAO.getAllCourses(); // Assuming a getAllCourses method in your CourseDAO
            System.out.println("==== All Courses ====");
            for (Course course : courses) {
                System.out.println("ID: " + course.getCourseId() +
                        ", Name: " + course.getCourseName() +
                        ", Code: " + course.getCourseCode() +
                        ", Instructor: " + course.getInstructorName());
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve courses: " + e.getMessage());
        }
    }
    
    private static void addStudent() {
        try {
            System.out.print("Enter First Name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter Last Name: ");
            String lastName = scanner.nextLine();
            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Phone Number: ");
            String phoneNumber = scanner.nextLine();

            Student student = new Student(0, firstName, lastName, Date.valueOf(dob), email, phoneNumber);
            studentDAO.addStudent(student);
            System.out.println("Student added successfully: " + firstName + " " + lastName);
        } catch (Exception e) {
            System.out.println("Failed to add student: " + e.getMessage());
        }
    }

    private static void addCourse() {
        try {
            System.out.print("Enter Course Name: ");
            String courseName = scanner.nextLine();
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine();
            System.out.print("Enter Instructor Name: ");
            String instructorName = scanner.nextLine();

            Course course = new Course(0, courseName, courseCode, instructorName);
            courseDAO.addCourse(course);
            System.out.println("Course added successfully: " + courseName);
        } catch (Exception e) {
            System.out.println("Failed to add course: " + e.getMessage());
        }
    }

    private static void enrollStudentInCourse() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            System.out.print("Enter Course ID: ");
            int courseId = scanner.nextInt();

            // Check if student exists
            Student student = studentDAO.getStudent(studentId);
            if (student == null) {
                throw new StudentNotFoundException("Student with ID " + studentId + " not found.");
            }

            // Check if course exists
            Course course = courseDAO.getCourse(courseId);
            if (course == null) {
                throw new CourseNotFoundException("Course with ID " + courseId + " not found.");
            }

            // Format the current date as a String (yyyy-MM-dd format)
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

            // Enroll the student
            Enrollment enrollment = new Enrollment(0, student, course, currentDate);
            enrollmentDAO.addEnrollment(enrollment);
            System.out.println("Student " + student.getFirstName() + " successfully enrolled in course: " + course.getCourseName());
        } catch (Exception e) {
            System.out.println("Failed to enroll student: " + e.getMessage());
        }
    }



    private static void assignTeacherToCourse() throws TeacherNotFoundException, CourseNotFoundException, InvalidTeacherDataException {
        System.out.print("Enter Teacher ID: ");
        int teacherId = scanner.nextInt();
        System.out.print("Enter Course ID: ");
        int courseId = scanner.nextInt();

        // Retrieve teacher and course details
        Teacher teacher = teacherDAO.getTeacher(teacherId);
        Course course = courseDAO.getCourse(courseId);

        if (teacher != null && course != null) {
            courseDAO.assignTeacher(courseId, teacher);
            System.out.println("Teacher assigned successfully to course: " + course.getCourseName());
        } else {
            System.out.println("Invalid teacher or course ID.");
        }
    }

    private static void makePayment() throws StudentNotFoundException, InvalidPaymentDataException {
        System.out.print("Enter Student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter Payment Amount: ");
        double amount = scanner.nextDouble();
        System.out.print("Enter Payment Date (YYYY-MM-DD): ");
        String paymentDate = scanner.next();  // Dummy date format

        Student student = studentDAO.getStudent(studentId);
        if (student != null) {
            Payment payment = new Payment(0, studentId, amount, new java.sql.Date(System.currentTimeMillis()));
            paymentDAO.recordPayment(payment);
            System.out.println("Payment recorded successfully for student: " + student.getFirstName());
        } else {
            System.out.println("Invalid student ID.");
        }
    }

    private static void generateEnrollmentReport() throws CourseNotFoundException {
        System.out.print("Enter Course ID to generate enrollment report: ");
        int courseId = scanner.nextInt();

        List<Enrollment> enrollments = courseDAO.getEnrollments(courseId);
        System.out.println("Enrollment Report for Course ID " + courseId + ":");
        for (Enrollment enrollment : enrollments) {
            System.out.println("Student ID: " + enrollment.getStudent().getStudentId() +
                    ", Name: " + enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());
        }
    }

    private static void generatePaymentReport() throws StudentNotFoundException {
        System.out.print("Enter Student ID to generate payment report: ");
        int studentId = scanner.nextInt();

        List<Payment> payments = paymentDAO.getPaymentsForStudent(studentId);
        System.out.println("Payment Report for Student ID " + studentId + ":");
        
        if (payments.isEmpty()) {
            System.out.println("No payments found for this student.");
        } else {
            for (Payment payment : payments) {
                System.out.println("Payment ID: " + payment.getPaymentId() +
                        ", Amount: " + payment.getAmount() + ", Date: " + payment.getPaymentDate());
            }
        }
    }

    private static void calculateCourseStatistics() throws CourseNotFoundException, PaymentNotFoundException {
        System.out.print("Enter Course ID to calculate statistics: ");
        int courseId = scanner.nextInt();

        List<Enrollment> enrollments = courseDAO.getEnrollments(courseId);
        List<Payment> payments = paymentDAO.getPaymentsForCourse(courseId);

        System.out.println("Course Statistics for Course ID " + courseId + ":");
        System.out.println("Number of Enrollments: " + enrollments.size());
        double totalPayments = payments.stream().mapToDouble(Payment::getAmount).sum();
        System.out.println("Total Payments: " + totalPayments);
    }
}
