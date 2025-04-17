package entity;

public class Enrollment {
    private int enrollmentId;
    private Student student;
    private Course course;
    private String enrollmentDate;

    // Constructor
    public Enrollment(int enrollmentId, Student student, Course course, String enrollmentDate) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
    }
    
    

	public int getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(int enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public String getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(String enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

    
}
