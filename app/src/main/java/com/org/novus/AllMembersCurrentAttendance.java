package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllMembersCurrentAttendance extends AppCompatActivity {
    ArrayList<AttendanceData> attendenceList;
    ListView AttendanceListView;
    String Uid;

    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members_current_attendance);
        attendenceList=new ArrayList<>();
        AttendanceListView=(ListView)findViewById(R.id.AttendanceListView);
        arrayAdapter=new ArrayAdapter(this,R.layout.date_wise_attendance_list_item,R.id.MemberName,attendenceList)
        {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                TextView MemberName=(TextView)view.findViewById(R.id.MemberName);

                TextView AttendanceStatus=(TextView)view.findViewById(R.id.AttendanceStatus);

                MemberName.setText(attendenceList.get(position).name);

                AttendanceStatus.setText(attendenceList.get(position).Total_Attended);

                return view;
            }
        };
        AttendanceListView.setAdapter(arrayAdapter);
        LoadAttendances();
    }

    public void LoadAttendances()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading Data");
        progressDialog.show();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String Attendance=dataSnapshot1.child("Attendance").getValue().toString()+"%";
                    String name=dataSnapshot1.child("name").getValue().toString();

                    attendenceList.add(new AttendanceData(name,"NA","NA",Attendance,"NA"));
                    progressDialog.dismiss();
                }
                arrayAdapter.notifyDataSetChanged();
                AttendanceListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
