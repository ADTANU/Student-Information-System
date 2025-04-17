package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import entity.Course;
import entity.Enrollment;
import entity.Student;
import exception.CourseNotFoundException;
import exception.DuplicateEnrollmentException;
import exception.InvalidEnrollmentDataException;
import exception.StudentNotFoundException;
import util.DBConnUtil;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    private Set<String> enrolledStudentCourses = new HashSet<>();

    @Override
    public Student getStudent(int enrollmentId) throws StudentNotFoundException {
        Student student = null;
        String sql = "SELECT s.* FROM students s JOIN enrollments e ON s.StudentID = e.StudentID WHERE e.EnrollmentID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, enrollmentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int studentId = resultSet.getInt("StudentID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                Date dateOfBirth = resultSet.getDate("DateOfBirth");
                String email = resultSet.getString("Email");
                String phoneNumber = resultSet.getString("PhoneNumber");

                student = new Student(studentId, firstName, lastName, dateOfBirth, email, phoneNumber);
            } else {
                throw new StudentNotFoundException("Student not found for enrollment ID: " + enrollmentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new StudentNotFoundException("Error retrieving student data.");
        }

        return student;
    }

    @Override
    public Course getCourse(int enrollmentId) throws CourseNotFoundException {
        Course course = null;
        String sql = "SELECT c.* FROM courses c JOIN enrollments e ON c.CourseID = e.CourseID WHERE e.EnrollmentID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, enrollmentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int courseId = resultSet.getInt("CourseID");
                String courseName = resultSet.getString("CourseName");
                String courseCode = resultSet.getString("CourseCode");
                String instructorName = resultSet.getString("InstructorName");

                course = new Course(courseId, courseName, courseCode, instructorName);
            } else {
                throw new CourseNotFoundException("Course not found for enrollment ID: " + enrollmentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CourseNotFoundException("Error retrieving course data.");
        }

        return course;
    }

    @Override
    public void addEnrollment(Enrollment enrollment) throws InvalidEnrollmentDataException, DuplicateEnrollmentException {
        if (enrollment == null || enrollment.getStudent() == null || enrollment.getCourse() == null) {
            throw new InvalidEnrollmentDataException("Enrollment data is invalid.");
        }

        String studentCourseKey = enrollment.getStudent().getStudentId() + "-" + enrollment.getCourse().getCourseId();
        if (enrolledStudentCourses.contains(studentCourseKey)) {
            throw new DuplicateEnrollmentException("Student is already enrolled in the course.");
        }

        String sql = "INSERT INTO enrollments (StudentID, CourseID, EnrollmentDate) VALUES (?, ?, ?)";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, enrollment.getStudent().getStudentId());
            statement.setInt(2, enrollment.getCourse().getCourseId());
            statement.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));  

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                enrolledStudentCourses.add(studentCourseKey);
                System.out.println("Enrollment added successfully.");
            } else {
                throw new InvalidEnrollmentDataException("Failed to add enrollment.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidEnrollmentDataException("Error occurred while adding enrollment.");
        }
    }
}
