import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.io.FileWriter;

public class Main {
    static ArrayList<Instructor> instructorsList = new ArrayList<>();
    static ArrayList<Course> coursesList = new ArrayList<>();
    static ArrayList<Classroom> classroomList = new ArrayList<>();

    public static void main(String[] args) {
        String slotTemp;
        boolean intructorAvailablity, classroomAvailablity;
        Classroom classroom;
        String timeSlot;
        int placementHours;
        int flag=0;

        readInstructors();
        readCources();
        readClassrooms();

        try {
            FileWriter detailsWriter = new FileWriter("C:\\Users\\devba\\IdeaProjects\\BeePlan\\scheduleDetails.txt");


        for (Course c : coursesList) {
            for(int day = 1; day <= 5; day++) {
                for (int session = 1; session <= 8; session++) {
                    slotTemp = Integer.toString(day) + "-" + Integer.toString(session);
                    if (c.instructor.avaliableSessions.checkAvaliablity(slotTemp)) intructorAvailablity = true;
                    else intructorAvailablity = false;

                    if(intructorAvailablity) {
                        for (Classroom cr : classroomList) {
                            classroomLoop:
                            for (int i = 0; i < c.theoricalHours ; i++) {
                                if(cr.avaliableSessions.checkAvaliablity(slotTemp)) {flag++;}
                                else{
//                                    System.out.println("Yerleştirme Başarısız: " + c.courseCode + " " + cr.classroomCode + " " + slotTemp + "(theorical hours)");
                                    break classroomLoop;
                                }
                                if (flag == c.theoricalHours) detailsWriter.write("CourseCode:" + c.courseCode + "Instructor:" + c.instructor + "Classroom:" + cr);
                            }
                        }
                    }


                }
            }
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            // Programı 3 saniye (3000 milisaniye) beklet
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            // Eğer başka bir thread bu uyku sürecini keserse burası çalışır
            System.err.println("Bekleme kesildi: " + e.getMessage());
        }
    }

    public static void readInstructors(){
        Scanner input = new Scanner(System.in);
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\InstructorsDocument.txt";
        String parts[];

        try(BufferedReader br = new BufferedReader(new FileReader(documentDirectory))){
            String line;
            while((line = br.readLine()) != null){
                parts = line.split("#");
                Instructor newInstructor = new Instructor(parts[0], parts[1], parts[2]);
                instructorsList.add(newInstructor);
                //System.out.println("Instructor name: " + newInstructor.name + "(" + newInstructor.fromWhere + ")");
                //newInstructor.avaliableSessions.printTimetable();
//                newInstructor.printInstructor();
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
        input.close();
    }

    public static void readCources(){
        String instructorName;
        Scanner input = new Scanner(System.in);
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\CourcesDocument.txt";
        String parts[];

        try(BufferedReader br = new BufferedReader(new FileReader(documentDirectory))){
            String line;
            while((line = br.readLine()) != null){
                parts = line.split("#");
                instructorName = parts[6];

                Course newCourse = new Course(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], findInstructor(instructorsList, instructorName));
                coursesList.add(newCourse);
                //newCourse.printCourse();
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
        input.close();
    }

    public static void readClassrooms(){
        String instructorName;
        Scanner input = new Scanner(System.in);
        String documentDirectory = "C:\\Users\\devba\\IdeaProjects\\BeePlan\\ClassroomsDocument.txt";
        String parts[];

        try(BufferedReader br = new BufferedReader(new FileReader(documentDirectory))){
            String line;
            while((line = br.readLine()) != null){
                parts = line.split("#");
                Classroom newClassroom = new Classroom(parts[0], parts[1]);
                classroomList.add(newClassroom);
                //System.out.println("newClassroom Code: " + newClassroom.classroomCode);
                //newClassroom.avaliableSessions.printTimetable();
            }
        } catch (IOException e) {
            System.err.println("Dosya okunurken bir hata oluştu: " + e.getMessage());
        }
        input.close();
    }

    public static Instructor findInstructor(ArrayList<Instructor> instructorsList, String instructorName){
        for(Instructor instructor : instructorsList){
            if(instructor.name.equals(instructorName)){
                return instructor;
            }
        }
        return null;
    }
}