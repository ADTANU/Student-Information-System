package entity;

public class Course {
	private String courseName;
	private String courseCode;
	private String instructorName;
	private int courseId;
	
	// Constructor
	public Course(int courseId, String courseName, String courseCode, String instructorName) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.courseCode = courseCode;
		this.instructorName = instructorName;
	}
    public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getInstructorName() {
		return instructorName;
	}

	public void setInstructorName(String instructorName) {
		this.instructorName = instructorName;
	}


    // Getters and Setters (Optional, depending on requirements)
}
