package dao;

import java.util.Date;
import java.util.List;

import entity.Payment;
import entity.Student;
import exception.InvalidPaymentDataException;
import exception.PaymentNotFoundException;
import exception.StudentNotFoundException;

public interface PaymentDAO {

	void recordPayment(Payment payment) throws InvalidPaymentDataException;
	
    Student getStudent(Payment payment) throws StudentNotFoundException;

    double getPaymentAmount(Payment payment) throws PaymentNotFoundException;

    Date getPaymentDate(Payment payment) throws PaymentNotFoundException;
    
    List<Payment> getPaymentsForStudent(int studentId) throws StudentNotFoundException;
    
    List<Payment> getPaymentsForCourse(int courseId) throws PaymentNotFoundException; 
}
