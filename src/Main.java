import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.io.FileWriter;

public class Main {
    static Timetable generatedScheduleTimetable = new Timetable();
    static ArrayList<Instructor> instructorsList = new ArrayList<>();
    static ArrayList<Course> coursesList = new ArrayList<>();
    static ArrayList<Classroom> classroomList = new ArrayList<>();

    public static void main(String[] args) {

        boolean instructorAvailable = false, classroomAvailable = false;

        String slotTemp;
        Classroom availableClassroom = null;

        readInstructors();
        readCources();
        readClassrooms();
        readCommonCourses();

        try {
            // Dosyayı temizleme
            try {
                FileWriter cleaner = new FileWriter("C:\\Users\\devba\\IdeaProjects\\BeePlan\\scheduleDetails.txt", false);
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
                        // Pratik ders 40 kişiden fazla ise, gruplara ayır
                        int numGroups = (int) Math.ceil((double) c.studentEstimated / 40);
                        for (int i = 1; i <= numGroups; i++) {
                            // Her grup için ayrı bir practical slot ekleyelim
                            if (!scheduleCourse(detailsWriter, c, "practical (" + i + ")", c.practicalHours)) {
                                System.err.println("Failed to schedule practical slots for: " + c.courseCode + " group " + i);
                            }
                        }
                    } else {
                        // 40 veya daha az öğrenci varsa, tek bir practical ders yerleştir
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

        generatedScheduleTimetable.printTimetable();

        try {
            // Programı 3 saniye (3000 milisaniye) beklet
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            System.err.println("Bekleme kesildi: " + e.getMessage());
        }
    }

    public static void readCommonCourses() {
        Scanner input = new Scanner(System.in);
        String commonCourses = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\commonCourses.txt";
        String parts[], _parts[];

        try (BufferedReader br = new BufferedReader(new FileReader(commonCourses))) {
            String line;
            while ((line = br.readLine()) != null) {
                parts = line.split("#");
                _parts = parts[2].split(",");
                for(String slotTemp: _parts) {
                    generatedScheduleTimetable.changeStatus(slotTemp, false);
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
        input.close();
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
                            detailsWriter.write(dayStr + "-" + (session+i));
                            if (i != hours - 1) {
                                detailsWriter.write(",");
                            }
                            if (i == hours - 1) {
                                detailsWriter.write("\n");
                            }
                        }
                        return true; // Başarılı bir yerleştirme sonrası döngüden çık
                    }
                }
            }
        }
        // Uygun bir yer bulunamadığında log yaz
        System.err.println("Failed to schedule " + type + " slots for: " + c.courseCode + " (" + c.courseName + ")");
        return false; // Uygun bir yer bulunamadı
    }

    public static void readInstructors() {
        Scanner input = new Scanner(System.in);
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\InstructorsDocument.txt";
        String parts[];

        try (BufferedReader br = new BufferedReader(new FileReader(documentDirectory))) {
            String line;
            while ((line = br.readLine()) != null) {
                parts = line.split("#");
                Instructor newInstructor = new Instructor(parts[0], parts[1], parts[2]);
                instructorsList.add(newInstructor);
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
        input.close();
    }

    public static void readCources() {
        String instructorName;
        Scanner input = new Scanner(System.in);
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\CourcesDocument.txt";
        String parts[];

        try (BufferedReader br = new BufferedReader(new FileReader(documentDirectory))) {
            String line;
            while ((line = br.readLine()) != null) {
                parts = line.split("#");
                instructorName = parts[6];

                Course newCourse = new Course(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], findInstructor(instructorsList, instructorName));
                coursesList.add(newCourse);
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
        input.close();
    }

    public static void readClassrooms() {
        String instructorName;
        Scanner input = new Scanner(System.in);
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\ClassroomsDocument.txt";
        String parts[];

        try (BufferedReader br = new BufferedReader(new FileReader(documentDirectory))) {
            String line;
            while ((line = br.readLine()) != null) {
                parts = line.split("#");
                Classroom newClassroom = new Classroom(parts[0], parts[1]);
                classroomList.add(newClassroom);
//                newClassroom.avaliableSessions.printTimetable();
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
        input.close();
    }

    public static Instructor findInstructor(ArrayList<Instructor> instructorsList, String instructorName) {
        for (Instructor instructor : instructorsList) {
            if (instructor.name.equals(instructorName)) {
                return instructor;
            }
        }
        return null;
    }

    public static boolean checkConsecutiveSlots(Instructor instructor, Classroom classroom, String day, int startSession, int requiredSlots) {
        for (int i = 0; i < requiredSlots; i++) {
            String slot = day + "-" + (startSession + i);
            if (!instructor.avaliableSessions.checkAvaliablity(slot)) {
//                System.err.println("Instructor conflict: " + instructor.name + " not available at " + slot);
                return false;
            }
            if (!classroom.avaliableSessions.checkAvaliablity(slot)) {
//                System.err.println("Classroom conflict: " + classroom.classroomCode + " not available at " + slot);
                return false;
            }
        }
        return true;
    }

    public static void reserveConsecutiveSlots(Instructor instructor, Classroom classroom, String day, int startSession, int requiredSlots) {
        for (int i = 0; i < requiredSlots; i++) {
            String slot = day + "-" + (startSession + i);
            instructor.avaliableSessions.changeStatus(slot, false);
            classroom.avaliableSessions.changeStatus(slot, false);
        }
    }
}
