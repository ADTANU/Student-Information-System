package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Payment;
import entity.Student;
import exception.InvalidPaymentDataException;
import exception.PaymentNotFoundException;
import exception.StudentNotFoundException;
import util.DBConnUtil;

public class PaymentDAOImpl implements PaymentDAO {

    // Method to record a payment for a student
    @Override
    public void recordPayment(Payment payment) throws InvalidPaymentDataException {
        if (payment == null || payment.getAmount() <= 0 || payment.getStudentId() <= 0) {
            throw new InvalidPaymentDataException("Invalid payment data. Amount or Student ID cannot be zero or negative.");
        }

        // Updated SQL query with correct column names
        String sql = "INSERT INTO payments (StudentID, Amount, PaymentDate) VALUES (?, ?, ?)";  // Corrected column names

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, payment.getStudentId());  // Corrected column name to StudentID
            statement.setDouble(2, payment.getAmount());  // Correct column name for amount
            statement.setDate(3, Date.valueOf(payment.getPaymentDate().toString()));  // Corrected column name for PaymentDate

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Payment recorded: " + payment.getAmount() + " for student ID: " + payment.getStudentId());
            } else {
                throw new InvalidPaymentDataException("Failed to record payment.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidPaymentDataException("Error occurred while recording the payment.");
        }
    }

    // Method to get payment amount by payment ID
    @Override
    public double getPaymentAmount(Payment payment) throws PaymentNotFoundException {
        double amount = 0.0;
        String sql = "SELECT amount FROM payments WHERE payment_id = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, payment.getPaymentId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                amount = resultSet.getDouble("amount");
            } else {
                throw new PaymentNotFoundException("Payment not found for payment ID: " + payment.getPaymentId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PaymentNotFoundException("Error retrieving payment amount.");
        }

        return amount;
    }

    // Method to get a list of payments for a student
    @Override
    public List<Payment> getPaymentsForStudent(int studentId) throws StudentNotFoundException {
        List<Payment> studentPayments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE StudentID = ?";  // Corrected to use StudentID

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new StudentNotFoundException("No payments found for student ID: " + studentId);
            }

            do {
                int paymentId = resultSet.getInt("PaymentID");  // Corrected column name to PaymentID
                double amount = resultSet.getDouble("Amount");  // Corrected column name to Amount
                Date paymentDate = resultSet.getDate("PaymentDate");  // Corrected column name to PaymentDate

                Payment payment = new Payment(paymentId, studentId, amount, paymentDate);
                studentPayments.add(payment);
            } while (resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new StudentNotFoundException("Error retrieving payments for student ID: " + studentId);
        }

        return studentPayments;
    }


    // Method to get a list of payments for a course
    @Override
    public List<Payment> getPaymentsForCourse(int courseId) throws PaymentNotFoundException {
        List<Payment> coursePayments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                     "JOIN enrollments e ON p.StudentID = e.StudentID " +  // Join with enrollments table using StudentID
                     "WHERE e.CourseID = ?";  // Corrected column name to CourseID

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new PaymentNotFoundException("No payments found for course ID: " + courseId);
            }

            do {
                int paymentId = resultSet.getInt("PaymentID");  // Corrected column name to PaymentID
                int studentId = resultSet.getInt("StudentID");  // Corrected column name to StudentID
                double amount = resultSet.getDouble("Amount");  // Corrected column name to Amount
                Date paymentDate = resultSet.getDate("PaymentDate");  // Corrected column name to PaymentDate

                Payment payment = new Payment(paymentId, studentId, amount, paymentDate);
                coursePayments.add(payment);
            } while (resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PaymentNotFoundException("Error retrieving payments for course ID: " + courseId);
        }

        return coursePayments;
    }


    // Method to get payment date by payment ID
    @Override
    public Date getPaymentDate(Payment payment) throws PaymentNotFoundException {
        Date paymentDate = null;
        String sql = "SELECT payment_date FROM payments WHERE payment_id = ?";

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, payment.getPaymentId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                paymentDate = resultSet.getDate("payment_date");
            } else {
                throw new PaymentNotFoundException("Payment not found for payment ID: " + payment.getPaymentId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PaymentNotFoundException("Error retrieving payment date.");
        }

        return paymentDate;
    }

    // Method to get student details for a payment
    @Override
    public Student getStudent(Payment payment) throws StudentNotFoundException {
        Student student = null;
        String sql = "SELECT * FROM students WHERE studentid = ?";  // Corrected column name to student_id

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, payment.getStudentId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int studentId = resultSet.getInt("studentid");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Date dateOfBirth = resultSet.getDate("date_of_birth");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone_number");

                student = new Student(studentId, firstName, lastName, dateOfBirth, email, phoneNumber);
            } else {
                throw new StudentNotFoundException("Student not found for student ID: " + payment.getStudentId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new StudentNotFoundException("Error retrieving student data.");
        }

        return student;
    }
}
