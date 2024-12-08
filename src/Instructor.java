import java.sql.Time;

public class Instructor {
    public String name;
    public String fromWhere;
    public Timetable avaliableSessions;
    public int[] dailyTheoricalLectures = { 0, 0, 0, 0, 0 };

    public Instructor(String name, String fromWhere, String avaliableSessionsString) {
        String[] avaliableSessionsArr = avaliableSessionsString.split(",");
        this.name = name;
        this.fromWhere = fromWhere;
        this.avaliableSessions = new Timetable();
        for(int i = 0; i < avaliableSessionsArr.length; i++){
            avaliableSessions.changeStatus(avaliableSessionsArr[i], false);
        }
    }

    public void printInstructor() {
        System.out.println("Name: " + this.name + " fromWhere: " + this.fromWhere);
    }
}