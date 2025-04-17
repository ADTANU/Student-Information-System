package dao;

import entity.Course;
import entity.Enrollment;
import entity.Student;
import exception.CourseNotFoundException;
import exception.DuplicateEnrollmentException;
import exception.InvalidEnrollmentDataException;
import exception.StudentNotFoundException;

public interface EnrollmentDAO {
    Student getStudent(int enrollmentId) throws StudentNotFoundException;
    Course getCourse(int enrollmentId) throws CourseNotFoundException; 
    void addEnrollment(Enrollment enrollment) throws InvalidEnrollmentDataException, DuplicateEnrollmentException; 
}
