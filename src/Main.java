import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Main {
    static Timetable generatedScheduleTimetable = new Timetable();
    static ArrayList<Instructor> instructorsList = new ArrayList<>();
    static ArrayList<Course> coursesList = new ArrayList<>();
    static ArrayList<Classroom> classroomList = new ArrayList<>();

    public static void main(String[] args) {

        readInstructors();
        readCourses();
        readClassrooms();
        readCommonCourses();

        try {
            // Dosyayı temizleme
            try (FileWriter cleaner = new FileWriter("C:\\Users\\devba\\IdeaProjects\\BeePlan\\scheduleDetails.txt", false)) {
                cleaner.close();
            } catch (IOException e) {
                System.err.println("Dosya temizlenirken bir hata oluştu: " + e.getMessage());
            }

            FileWriter detailsWriter = new FileWriter("C:\\Users\\devba\\IdeaProjects\\BeePlan\\scheduleDetails.txt");

            for (Course c : coursesList) {
                if (c.instructor == null) {
                    System.err.println("Instructor not found for course: " + c.courseCode);
                    continue;
                }

                // Teorik dersler
                if (c.theoricalHours > 0) {
                    if (!scheduleCourse(detailsWriter, c, "theorical", c.theoricalHours)) {
                        System.err.println("Failed to schedule theorical slots for: " + c.courseCode);
                    }
                }

                // Pratik dersler
                if (c.practicalHours > 0) {
                    if (c.studentEstimated > 40) {
                        // 40 kişiden fazla, gruplara ayır
                        int numGroups = (int) Math.ceil((double) c.studentEstimated / 40);
                        for (int i = 1; i <= numGroups; i++) {
                            if (!scheduleCourse(detailsWriter, c, "practical (" + i + ")", c.practicalHours)) {
                                System.err.println("Failed to schedule practical slots for: " + c.courseCode + " group " + i);
                            }
                        }
                    } else {
                        // 40 veya daha az öğrenci
                        if (!scheduleCourse(detailsWriter, c, "practical", c.practicalHours)) {
                            System.err.println("Failed to schedule practical slots for: " + c.courseCode);
                        }
                    }
                }
            }

            detailsWriter.close();
            System.out.println("Tüm dersler başarıyla yerleştirildi!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Eğitmenlerin günlük teorik ders saatlerini yazdırma
        for (Instructor i : instructorsList) {
            System.out.println(i.name + " - Günlük Teorik Dersler:");
            for (int a = 0; a <= 4; a++) {
                System.out.print(i.dailyTheoricalLectures[a] + "\t");
            }
            System.out.println();
        }
    }

    public static boolean scheduleCourse(FileWriter detailsWriter, Course c, String type, int hours) throws IOException {
        for (int day = 1; day <= 5; day++) {
            for (int session = 1; session <= (8 - hours + 1); session++) {
                for (Classroom classroom : classroomList) { // Tüm sınıfları dene
                    String dayStr = Integer.toString(day);
                    if (checkConsecutiveSlots(c.instructor, classroom, dayStr, session, hours)) {
                        reserveConsecutiveSlots(c.instructor, classroom, dayStr, session, hours);
                        detailsWriter.write(c.courseCode + "#" + c.courseName + "#" + type + "#" + c.instructor.name + "#" + classroom.classroomCode + "#");
                        for (int i = 0; i < hours; i++) {
                            detailsWriter.write(dayStr + "-" + (session + i));
                            if (i != hours - 1) detailsWriter.write(",");
                        }
                        detailsWriter.write("\n");
                        return true;
                    }
                }
            }
        }
        System.err.println("Failed to schedule " + type + " slots for: " + c.courseCode + " (" + c.courseName + ")");
        return false;
    }

    public static boolean checkConsecutiveSlots(Instructor instructor, Classroom classroom, String day, int startSession, int requiredSlots) {
        int dayIndex = Integer.parseInt(day) - 1;

        // Günlük teorik ders sınırı kontrolü
        if (instructor.dailyTheoricalLectures[dayIndex] + requiredSlots > 4) {
            return false;
        }

        for (int i = 0; i < requiredSlots; i++) {
            String slot = day + "-" + (startSession + i);
            if (!instructor.avaliableSessions.checkAvaliablity(slot)) {
                return false;
            }
            if (!classroom.avaliableSessions.checkAvaliablity(slot)) {
                return false;
            }
        }
        return true;
    }

    public static void reserveConsecutiveSlots(Instructor instructor, Classroom classroom, String day, int startSession, int requiredSlots) {
        int dayIndex = Integer.parseInt(day) - 1;
        instructor.dailyTheoricalLectures[dayIndex] += requiredSlots;

        for (int i = 0; i < requiredSlots; i++) {
            String slot = day + "-" + (startSession + i);
            instructor.avaliableSessions.changeStatus(slot, false);
            classroom.avaliableSessions.changeStatus(slot, false);
        }
    }

    public static void readInstructors() {
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\InstructorsDocument.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(documentDirectory))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                instructorsList.add(new Instructor(parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
    }

    public static void readCourses() {
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\CourcesDocument.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(documentDirectory))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                Instructor instructor = findInstructor(parts[6]);
                coursesList.add(new Course(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], instructor));
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
    }

    public static void readClassrooms() {
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\ClassroomsDocument.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(documentDirectory))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                classroomList.add(new Classroom(parts[0], parts[1]));
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
    }

    public static void readCommonCourses() {
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\commonCourses.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(documentDirectory))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                String[] slots = parts[2].split(",");
                for (String slot : slots) {
                    generatedScheduleTimetable.changeStatus(slot, false);
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
    }

    public static Instructor findInstructor(String instructorName) {
        for (Instructor instructor : instructorsList) {
            if (instructor.name.equals(instructorName)) {
                return instructor;
            }
        }
        return null;
    }
}