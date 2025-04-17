package dao;

import java.util.List;

import entity.Course;
import entity.Payment;
import entity.Student;
import exception.EnrollmentException;
import exception.InvalidDateFormatException;
import exception.PaymentException;
import exception.StudentNotFoundException;
import exception.UpdateStudentInfoException;

public interface StudentDAO {
	
	Student getStudent(int studentId) throws StudentNotFoundException;
    void enrollInCourse(int studentId, Course course) throws EnrollmentException; 
    void updateStudentInfo(int studentId, String firstName, String lastName, String dateOfBirth, String email, String phoneNumber) throws UpdateStudentInfoException, InvalidDateFormatException; 
    void makePayment(int studentId, double amount, String paymentDate) throws PaymentException, InvalidDateFormatException;
    void displayStudentInfo(int studentId) throws StudentNotFoundException; 
    List<Course> getEnrolledCourses(int studentId) throws StudentNotFoundException; 
    List<Payment> getPaymentHistory(int studentId) throws StudentNotFoundException;
	void addStudent(Student student);
	List<Student> getAllStudents(); 
}
