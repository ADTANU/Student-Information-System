package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Course;
import entity.Teacher;
import exception.InvalidNameFormatException;
import exception.TeacherNotFoundException;
import util.DBConnUtil;

public class TeacherDAOImpl implements TeacherDAO {

    @Override
    public Teacher getTeacher(int teacherId) throws TeacherNotFoundException {
        Teacher teacher = null;
        String sql = "SELECT * FROM teachers WHERE TeacherID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, teacherId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String email = resultSet.getString("Email");
                teacher = new Teacher(teacherId, firstName, lastName, email);
            } else {
                throw new TeacherNotFoundException("Teacher with ID " + teacherId + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new TeacherNotFoundException("Error retrieving teacher with ID " + teacherId, e);
        }

        return teacher;
    }

    @Override
    public void updateTeacherInfo(int teacherId, String name, String email) throws InvalidNameFormatException, SQLException {
        String[] nameParts = name.split(" ", 2);
        if (nameParts.length != 2) {
            throw new InvalidNameFormatException("Invalid name format. Expected 'FirstName LastName'.");
        }

        String firstName = nameParts[0];
        String lastName = nameParts[1];

        String sql = "UPDATE teachers SET FirstName = ?, LastName = ?, Email = ? WHERE TeacherID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setInt(4, teacherId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Teacher information updated successfully.");
            } else {
                System.out.println("Failed to update teacher information.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating teacher with ID " + teacherId, e);
        }
    }

    @Override
    public void displayTeacherInfo(int teacherId) throws TeacherNotFoundException {
        Teacher teacher = getTeacher(teacherId);
        if (teacher != null) {
            System.out.println("Teacher Info:");
            System.out.println("ID: " + teacher.getTeacherId());
            System.out.println("Name: " + teacher.getFirstName() + " " + teacher.getLastName());
            System.out.println("Email: " + teacher.getEmail());
        } else {
            throw new TeacherNotFoundException("Teacher not found.");
        }
    }

    @Override
    public List<Course> getAssignedCourses(int teacherId) throws SQLException {
        List<Course> assignedCourses = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c " +
                     "JOIN teacher_courses tc ON c.course_id = tc.course_id " +
                     "WHERE tc.TeacherID = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, teacherId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                String courseCode = resultSet.getString("course_code");
                String instructorName = resultSet.getString("instructor_name");
                Course course = new Course(courseId, courseName, courseCode, instructorName);
                assignedCourses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving assigned courses for teacher with ID " + teacherId, e);
        }

        return assignedCourses;
    }

    @Override
    public void addTeacher(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teachers (TeacherID, FirstName, LastName, Email) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, teacher.getTeacherId());
            statement.setString(2, teacher.getFirstName());
            statement.setString(3, teacher.getLastName());
            statement.setString(4, teacher.getEmail());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Teacher added successfully.");
            }
        } catch (SQLException e)            {
            e.printStackTrace();
            throw new SQLException("Error adding teacher " + teacher.getFirstName() + " " + teacher.getLastName(), e);
        }
    }

    @Override
    public void assignCourseToTeacher(int teacherId, Course course) throws SQLException {
        String sql = "INSERT INTO teacher_courses (TeacherID, course_id) VALUES (?, ?)";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, teacherId);
            statement.setInt(2, course.getCourseId());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Course " + course.getCourseName() + " assigned to teacher ID: " + teacherId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error assigning course to teacher with ID " + teacherId, e);
        }
    }
}
