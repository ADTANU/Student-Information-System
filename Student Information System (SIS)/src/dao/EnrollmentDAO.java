package dao;

import entity.Course;
import entity.Enrollment;
import entity.Student;
import exception.CourseNotFoundException;
import exception.DuplicateEnrollmentException;
import exception.InvalidEnrollmentDataException;
import exception.StudentNotFoundException;

public interface EnrollmentDAO {
    Student getStudent(int enrollmentId) throws StudentNotFoundException; // Retrieves the student associated with the enrollment
    Course getCourse(int enrollmentId) throws CourseNotFoundException; // Retrieves the course associated with the enrollment
    void addEnrollment(Enrollment enrollment) throws InvalidEnrollmentDataException, DuplicateEnrollmentException; // Adds a new enrollment
}
