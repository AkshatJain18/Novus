package com.org.novus;

import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class RetrieveFirebaseData
{
    String name;
    String Department;
    String DateOfJoining;
    String Admin;
    String Attendance;
    String Performance;
    String Incharge;
    String status;
    boolean flag=false;
    public RetrieveFirebaseData(String Uid)
    {

        System.out.print(Uid);
        DatabaseReference mDatabase;

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Uid);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                 name=dataSnapshot.child("name").getValue().toString();
                 status="data snapshot k ander";
                 if(name.equals(""))
                 {
                     status="data not rtrved";
                 }
                 Admin=dataSnapshot.child("Admin").getValue().toString();
                Department=dataSnapshot.child("Department").getValue().toString();
                DateOfJoining=dataSnapshot.child("DOJ").getValue().toString();
                Attendance=dataSnapshot.child("Attendance").getValue().toString();
                Performance=dataSnapshot.child("Performance").getValue().toString();
                Incharge=dataSnapshot.child("Incharge").getValue().toString();
                flag=true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               // status="data not rtrved";
            }


        });
    }

    public String getName()
    {
        return name;
    }
    public String getAdmin()
    {
        return Admin;
    }
    public String getDepartment()
    {
        return Department;
    }
    public String getDateOfJoining()
    {
        return DateOfJoining;
    }
    public String getAttendance()
    {
        return Attendance;
    }
    public String getPerformance()
    {
        return Performance;
    }
    public String getIncharge()
    {
        return Incharge;
    }
}
