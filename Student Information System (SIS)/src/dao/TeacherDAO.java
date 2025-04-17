package dao;

import java.sql.SQLException;
import java.util.List;

import entity.Course;
import entity.Teacher;
import exception.InvalidNameFormatException;
import exception.TeacherNotFoundException;

public interface TeacherDAO {
	
	Teacher getTeacher(int teacherId) throws TeacherNotFoundException;
    void updateTeacherInfo(int teacherId, String name, String email) throws InvalidNameFormatException, SQLException; // Updates teacher information
    void displayTeacherInfo(int teacherId) throws TeacherNotFoundException; // Displays detailed information about the teacher
    List<Course> getAssignedCourses(int teacherId) throws SQLException; // Retrieves a list of courses assigned to the teacher
    void addTeacher(Teacher teacher) throws SQLException; // Adds a new teacher (optional for testing)
	void assignCourseToTeacher(int teacherId, Course course) throws SQLException;
}
