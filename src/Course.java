public class Course {
    public String courseCode;
    public String courseName;
    public Instructor instructor;
    public int studentEstimated;
    public int theoricalHours;
    public int practicalHours;
    public String lecType; // 1 -> zorunlu, 2 -> seçmeli, 3 -> servis

    public Course(String courseCode, String courseName, String studentEstimated, String theoricalHours, String practicalHours, String lecType, Instructor instructor) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.studentEstimated = Integer.parseInt(studentEstimated);
        this.theoricalHours = Integer.parseInt(theoricalHours);
        this.practicalHours = Integer.parseInt(practicalHours);
        this.lecType = lecType;
        this.instructor = instructor;
    }

    public void printCourse() {
        System.out.println("Course Code: " + this.courseCode + "\tCourse Name: " + this.courseName + "\tStudent Estimated: " + this.studentEstimated + "\tHours: " + this.theoricalHours + "/" + this.practicalHours + "\tLec Type: " + this.lecType + "\tInstructor: " + this.instructor.name);
    }
}