package entity;

import java.util.Date;

public class Payment {
    private int paymentId; // Unique payment ID
    private int studentId; // Reference to the Student ID
    private double amount; // Payment amount
    private Date paymentDate; // Date of payment as a Date object

    public Payment(int paymentId, int studentId, double amount, Date paymentDate) {
        this.paymentId = paymentId;
        this.studentId = studentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    // Method to retrieve the associated student (based on studentId)
    public Student getStudent() {
        // This assumes you will be handling the lookup via the DAO or service layer.
        return null; // Placeholder for actual Student retrieval logic.
    }
}
