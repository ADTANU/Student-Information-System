package dao;

import entity.Course;
import entity.Payment;
import entity.Student;
import exception.EnrollmentException;
import exception.InvalidDateFormatException;
import exception.PaymentException;
import exception.StudentNotFoundException;
import exception.UpdateStudentInfoException;
import util.DBConnUtil;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private boolean isConnectionValid(Connection connection) {
        try {
            if (connection == null || connection.isClosed()) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Student getStudent(int studentId) throws StudentNotFoundException {
        Student student = null;
        String sql = "SELECT * FROM students WHERE student_id = ?";  

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (!isConnectionValid(connection)) {
                throw new SQLException("Database connection failed.");
            }

            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                Date dateOfBirth = resultSet.getDate("DateOfBirth");
                String email = resultSet.getString("Email");
                String phoneNumber = resultSet.getString("PhoneNumber");

                student = new Student(studentId, firstName, lastName, dateOfBirth, email, phoneNumber);
            } else {
                throw new StudentNotFoundException("Student with ID " + studentId + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new StudentNotFoundException("Error fetching student: " + e.getMessage());
        }

        return student;
    }

    @Override
    public void enrollInCourse(int studentId, Course course) throws EnrollmentException {
        String sql = "INSERT INTO enrollments (student_id, CourseID) VALUES (?, ?)"; 

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (!isConnectionValid(connection)) {
                throw new SQLException("Database connection failed.");
            }

            statement.setInt(1, studentId);
            statement.setInt(2, course.getCourseId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected <= 0) {
                throw new EnrollmentException("Failed to enroll student in course.");
            } else {
                System.out.println("Student " + studentId + " enrolled in course: " + course.getCourseName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new EnrollmentException("Error enrolling student in course: " + e.getMessage());
        }
    }

    @Override
    public void updateStudentInfo(int studentId, String firstName, String lastName, String dateOfBirth, String email, String phoneNumber)
            throws UpdateStudentInfoException, InvalidDateFormatException {
        String sql = "UPDATE students SET FirstName = ?, LastName = ?, DateOfBirth = ?, Email = ?, PhoneNumber = ? WHERE student_id = ?";

        try {
            Date dob = new Date(dateFormat.parse(dateOfBirth).getTime());
            try (Connection connection = DBConnUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                if (!isConnectionValid(connection)) {
                    throw new SQLException("Database connection failed.");
                }

                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setDate(3, dob);
                statement.setString(4, email);
                statement.setString(5, phoneNumber);
                statement.setInt(6, studentId);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected <= 0) {
                    throw new UpdateStudentInfoException("Failed to update student information.");
                } else {
                    System.out.println("Student information updated successfully.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new UpdateStudentInfoException("Error updating student info: " + e.getMessage());
            }
        } catch (ParseException e) {
            throw new InvalidDateFormatException("Invalid date format for date of birth.");
        }
    }

    @Override
    public void makePayment(int studentId, double amount, String paymentDate) throws PaymentException, InvalidDateFormatException {
        String sql = "INSERT INTO payments (StudentID, Amount, PaymentDate) VALUES (?, ?, ?)"; 

        try {
            Date paymentDateObj = new Date(dateFormat.parse(paymentDate).getTime());
            try (Connection connection = DBConnUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                if (!isConnectionValid(connection)) {
                    throw new SQLException("Database connection failed.");
                }

                statement.setInt(1, studentId);
                statement.setDouble(2, amount);
                statement.setDate(3, paymentDateObj);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected <= 0) {
                    throw new PaymentException("Failed to record payment.");
                } else {
                    System.out.println("Payment of " + amount + " recorded for student " + studentId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new PaymentException("Error recording payment: " + e.getMessage());
            }
        } catch (ParseException e) {
            throw new InvalidDateFormatException("Invalid date format for payment date.");
        }
    }

    @Override
    public void displayStudentInfo(int studentId) throws StudentNotFoundException {
        Student student = getStudent(studentId);
        if (student != null) {
            System.out.println("Student Info:");
            System.out.println("ID: " + student.getStudentId());
            System.out.println("Name: " + student.getFirstName() + " " + student.getLastName());
            System.out.println("DOB: " + dateFormat.format(student.getDateOfBirth()));
            System.out.println("Email: " + student.getEmail());
            System.out.println("Phone: " + student.getPhoneNumber());
        } else {
            throw new StudentNotFoundException("Student with ID " + studentId + " not found.");
        }
    }

    @Override
    public List<Course> getEnrolledCourses(int studentId) throws StudentNotFoundException {
        List<Course> enrolledCourses = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c " +
                     "JOIN enrollments e ON c.CourseID = e.CourseID " +
                     "WHERE e.student_id = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (!isConnectionValid(connection)) {
                throw new SQLException("Database connection failed.");
            }

            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int courseId = resultSet.getInt("CourseID");
                String courseName = resultSet.getString("CourseName");
                String courseCode = resultSet.getString("CourseCode");
                String instructorName = resultSet.getString("InstructorName");
                Course course = new Course(courseId, courseName, courseCode, instructorName);
                enrolledCourses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new StudentNotFoundException("Error fetching enrolled courses: " + e.getMessage());
        }

        if (enrolledCourses.isEmpty()) {
            throw new StudentNotFoundException("No courses found for student ID: " + studentId);
        }

        return enrolledCourses;
    }

    @Override
    public List<Payment> getPaymentHistory(int studentId) throws StudentNotFoundException {
        List<Payment> paymentHistory = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE student_id = ?"; 

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (!isConnectionValid(connection)) {
                throw new SQLException("Database connection failed.");
            }

            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int paymentId = resultSet.getInt("PaymentID");
                double amount = resultSet.getDouble("Amount");
                Date paymentDate = resultSet.getDate("PaymentDate");

                Payment payment = new Payment(paymentId, studentId, amount, paymentDate);
                paymentHistory.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new StudentNotFoundException("Error fetching payment history: " + e.getMessage());
        }

        if (paymentHistory.isEmpty()) {
            throw new StudentNotFoundException("No payment history found for student ID: " + studentId);
        }

        return paymentHistory;
    }

    @Override
    public void addStudent(Student student) {
        String sql = "INSERT INTO students (FirstName, LastName, DateOfBirth, Email, PhoneNumber) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (!isConnectionValid(connection)) {
                throw new SQLException("Database connection failed.");
            }

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setDate(3, new java.sql.Date(student.getDateOfBirth().getTime()));
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhoneNumber());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student added successfully.");
            } else {
                System.out.println("Failed to add student.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        
        String query = "SELECT * FROM students"; 

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                Date dob = resultSet.getDate("DateOfBirth");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phonenumber");

                Student student = new Student(studentId, firstName, lastName, dob, email, phoneNumber);
                students.add(student);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }

        return students;
    }
}
