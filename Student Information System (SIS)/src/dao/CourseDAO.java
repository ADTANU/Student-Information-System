package dao;

import java.util.List;

import entity.Course;
import entity.Enrollment;
import entity.Teacher;
import exception.CourseNotFoundException;
import exception.InvalidCourseDataException;
import exception.InvalidTeacherDataException;
import exception.TeacherNotFoundException;

public interface CourseDAO {
	
	Course getCourse(int courseId) throws CourseNotFoundException; 
    void assignTeacher(int courseId, Teacher teacher) throws TeacherNotFoundException, InvalidTeacherDataException; // Assigns a teacher to the course
    void updateCourseInfo(int courseId, String courseCode, String courseName, String instructorName) throws InvalidCourseDataException, CourseNotFoundException; // Updates course information
    void displayCourseInfo(int courseId) throws CourseNotFoundException; // Displays detailed information about the course
    List<Enrollment> getEnrollments(int courseId) throws CourseNotFoundException; // Retrieves a list of student enrollments for the course
    Teacher getAssignedTeacher(int courseId) throws TeacherNotFoundException; // Retrieves the assigned teacher for the course
	void addCourse(Course course);
	List<Course> getAllCourses();
}
