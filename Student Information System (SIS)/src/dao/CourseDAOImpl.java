package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Course;
import entity.Enrollment;
import entity.Student;
import entity.Teacher;
import exception.CourseNotFoundException;
import exception.InvalidCourseDataException;
import exception.InvalidTeacherDataException;
import exception.TeacherNotFoundException;
import util.DBConnUtil;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public Course getCourse(int courseId) throws CourseNotFoundException {
        Course course = null;
        String sql = "SELECT c.CourseID, c.CourseName, c.CourseCode, c.InstructorName FROM courses c WHERE c.CourseID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String courseName = resultSet.getString("CourseName");
                String courseCode = resultSet.getString("CourseCode");
                String instructorName = resultSet.getString("InstructorName");

                course = new Course(courseId, courseName, courseCode, instructorName);
            } else {
                throw new CourseNotFoundException("Course with ID " + courseId + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CourseNotFoundException("Error retrieving course data for ID " + courseId);
        }

        return course;
    }

    @Override
    public void assignTeacher(int courseId, Teacher teacher) throws TeacherNotFoundException, InvalidTeacherDataException {
        if (teacher == null || teacher.getFirstName() == null || teacher.getLastName() == null) {
            throw new InvalidTeacherDataException("Teacher data is invalid.");
        }

        String sql = "UPDATE courses c SET c.InstructorName = ? WHERE c.CourseID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, teacher.getFirstName() + " " + teacher.getLastName());
            statement.setInt(2, courseId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new TeacherNotFoundException("Teacher with ID " + teacher.getTeacherId() + " not found.");
            }

            System.out.println("Teacher " + teacher.getFirstName() + " assigned to course with ID: " + courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCourseInfo(int courseId, String courseCode, String courseName, String instructorName) throws InvalidCourseDataException, CourseNotFoundException {
        if (courseCode == null || courseName == null || instructorName == null) {
            throw new InvalidCourseDataException("Course data is invalid.");
        }

        String sql = "UPDATE courses c SET c.CourseCode = ?, c.CourseName = ?, c.InstructorName = ? WHERE c.CourseID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, courseCode);
            statement.setString(2, courseName);
            statement.setString(3, instructorName);
            statement.setInt(4, courseId);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new CourseNotFoundException("Course with ID " + courseId + " not found.");
            }

            System.out.println("Course information updated successfully for course ID: " + courseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayCourseInfo(int courseId) throws CourseNotFoundException {
        String sql = "SELECT c.CourseID, c.CourseName, c.CourseCode, c.InstructorName FROM courses c WHERE c.CourseID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Course Info:");
                System.out.println("ID: " + resultSet.getInt("CourseID"));
                System.out.println("Name: " + resultSet.getString("CourseName"));
                System.out.println("Code: " + resultSet.getString("CourseCode"));
                System.out.println("Instructor: " + resultSet.getString("InstructorName"));
            } else {
                throw new CourseNotFoundException("Course with ID " + courseId + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Enrollment> getEnrollments(int courseId) throws CourseNotFoundException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.EnrollmentID, e.StudentID, s.FirstName, s.LastName, s.Email, s.PhoneNumber, e.EnrollmentDate, c.CourseName, c.CourseCode, t.FirstName AS TeacherFirstName, t.LastName AS TeacherLastName " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.StudentID = s.student_id " +
                     "JOIN courses c ON e.CourseID = c.CourseID " +
                     "LEFT JOIN teachers t ON c.InstructorName = CONCAT(t.FirstName, ' ', t.LastName) " + // Adjusting join based on instructor name
                     "WHERE e.CourseID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int enrollmentId = resultSet.getInt("EnrollmentID");
                int studentId = resultSet.getInt("StudentID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String email = resultSet.getString("Email");
                String phoneNumber = resultSet.getString("PhoneNumber");
                Date enrollmentDate = resultSet.getDate("EnrollmentDate");
                String courseName = resultSet.getString("CourseName");
                String courseCode = resultSet.getString("CourseCode");
                String teacherFirstName = resultSet.getString("TeacherFirstName");
                String teacherLastName = resultSet.getString("TeacherLastName");

                // Create Student and Course objects based on fetched data
                Student student = new Student(studentId, firstName, lastName, null, email, phoneNumber);
                Course course = new Course(courseId, courseName, courseCode, teacherFirstName + " " + teacherLastName);

                // Create Enrollment object and add it to the list
                enrollments.add(new Enrollment(enrollmentId, student, course, enrollmentDate.toString()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CourseNotFoundException("Error fetching enrollments for course ID " + courseId);
        }

        if (enrollments.isEmpty()) {
            throw new CourseNotFoundException("No enrollments found for course ID " + courseId);
        }

        return enrollments;
    }


    @Override
    public Teacher getAssignedTeacher(int courseId) throws TeacherNotFoundException {
        Teacher teacher = null;
        String sql = "SELECT c.InstructorName FROM courses c WHERE c.CourseID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String instructorName = resultSet.getString("InstructorName");
                String[] instructorNames = instructorName.split(" ");
                String firstName = instructorNames[0];
                String lastName = instructorNames.length > 1 ? instructorNames[1] : "";

                teacher = new Teacher(0, firstName, lastName, "email@example.com");
            } else {
                throw new TeacherNotFoundException("No teacher assigned for course ID " + courseId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teacher;
    }

    @Override
    public void addCourse(Course course) {
        String sql = "INSERT INTO courses (coursename, coursecode, instructorname) VALUES (?, ?, ?)";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, course.getCourseName());
            preparedStatement.setString(2, course.getCourseCode());
            preparedStatement.setString(3, course.getInstructorName());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Course added successfully to the database.");
            } else {
                System.out.println("Failed to add the course. No rows affected.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();

        // Query to fetch all courses
        String query = "SELECT * FROM courses"; // Ensure your table name is correct

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate through the result set and create Course objects
            while (resultSet.next()) {
                int courseId = resultSet.getInt("courseid"); // Adjust column names as per your DB schema
                String courseName = resultSet.getString("coursename");
                String courseCode = resultSet.getString("coursecode");
                String instructorName = resultSet.getString("instructorname");

                // Create a Course object and add it to the list
                Course course = new Course(courseId, courseName, courseCode, instructorName);
                courses.add(course);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }

        return courses;
    }

}
