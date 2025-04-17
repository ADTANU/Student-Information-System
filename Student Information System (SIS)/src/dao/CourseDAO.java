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
    void assignTeacher(int courseId, Teacher teacher) throws TeacherNotFoundException, InvalidTeacherDataException;
    void updateCourseInfo(int courseId, String courseCode, String courseName, String instructorName) throws InvalidCourseDataException, CourseNotFoundException; 
    void displayCourseInfo(int courseId) throws CourseNotFoundException; 
    List<Enrollment> getEnrollments(int courseId) throws CourseNotFoundException;
    Teacher getAssignedTeacher(int courseId) throws TeacherNotFoundException;
	void addCourse(Course course);
	List<Course> getAllCourses();
}
