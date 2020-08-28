package com.org.novus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class TotalAttendanceOfAllMembers extends AppCompatActivity {

    ListView AttendanceListView;
    String status;
    ArrayAdapter<AttendanceData> arrayAdapter;
    ArrayList<AttendanceData> members;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_attendance_of_all_members);

        IntialiseViews();
    }
    public void IntialiseViews()
    {

    }
}
