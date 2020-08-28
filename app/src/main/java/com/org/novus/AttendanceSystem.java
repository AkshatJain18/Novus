package com.org.novus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AttendanceSystem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_system);
    }

    public void MarkAttendance(View view)
    {
        Intent intent=new Intent(this,MarkAttendance.class);
        startActivity(intent);
    }

    public void ViewAttendanceRecordsDateWise(View view)
    {
        Intent intent=new Intent(this,DateWiseAttendanceRecords.class);
        startActivity(intent);

    }
    public void ViewAttendanceRecordsMembers(View view)
    {
        Intent intent=new Intent(this,AllMembersCurrentAttendance.class);
        startActivity(intent);

    }
}
