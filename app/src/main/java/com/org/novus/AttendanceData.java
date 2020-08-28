package com.org.novus;

public class AttendanceData {

    String name;
    String Uid,AttendanceStatus,Total_Attended,Total_Meetings;

    public AttendanceData(String name, String uid , String attendanceStatus,String total_Attended,String total_meetings) {
        this.name = name;
        this.Uid = uid;
        this.AttendanceStatus=attendanceStatus;
        this.Total_Attended=total_Attended;
        this.Total_Meetings=total_meetings;
    }
}
