public class Classroom {
    public String classroomCode;
    public Timetable avaliableSessions = new Timetable();

    public Classroom(String classroomCode, String avaliableSessionsString) {
        String[] avaliableSessionsArr = avaliableSessionsString.split(",");
        this.classroomCode = classroomCode;
        this.avaliableSessions = new Timetable();
        for(int i = 0; i < avaliableSessionsArr.length; i++){
            avaliableSessions.changeStatus(avaliableSessionsArr[i], false);
        }
    }
}
