package com.org.novus;

public class User {
    public String name;
    public String Attendance;
    public String Performance;
    public String Department;
    public String Admin;
    public String DOJ,post;
    public String Incharge,ProfileUrl,Path;
    public String Total_Attended,Total_Meetings,Removed;

    public User(String name,String Attendance,String Performance,String Department,String Admin,String DOJ,String Incharge, String profileUrl, String total_Attended,String total_Meetings,String Post,String Removed,String path)
    {
      this.name=name;
      this.Attendance=Attendance;
      this.Performance=Performance;
      this.Department=Department;
      this.Admin=Admin;
      this.DOJ=DOJ;
      this.Incharge=Incharge;
      this.ProfileUrl=profileUrl;
      this.Total_Attended=total_Attended;
      this.Total_Meetings=total_Meetings;
      this.post=Post;
      this.Removed=Removed;
      this.Path=path;
    }
}
