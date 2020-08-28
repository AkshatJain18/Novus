package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Attendance extends AppCompatActivity {

    ArrayList<IndividualAttendance> individualAttendances;
    String Uid;
    String OverAllAttendance;
    ProgressDialog progressDialog;
    ListView listView;
    ArrayAdapter arrayAdapter;
    FirebaseAuth firebaseAuth;
    TextView OverAllAttendanceTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        progressDialog=new ProgressDialog(this);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        //progressDialog.setButton(0,"please wait",null);

        listView=(ListView)findViewById(R.id.listview);
        OverAllAttendanceTextView =(TextView)findViewById(R.id.OverallAttendanceTextView);
        firebaseAuth=FirebaseAuth.getInstance();
        Uid=firebaseAuth.getUid();


        individualAttendances=new ArrayList();
        arrayAdapter = new ArrayAdapter(this, R.layout.individual_attendance, R.id.MeetingNameTextView, individualAttendances) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView MeetingName=(TextView)view.findViewById(R.id.MeetingNameTextView);

                TextView status=(TextView)view.findViewById(R.id.AttendanceStatusTextView);

                TextView date=(TextView)view.findViewById(R.id.DateTextView);

                MeetingName.setText(individualAttendances.get(position).MeetingName);

                status.setText(individualAttendances.get(position).status);

                date.setText(individualAttendances.get(position).Date);

                return view;
            }
        };
        listView.setAdapter(arrayAdapter);
        LoadData();



    }
    public void LoadData()
    {
        progressDialog.setMessage("loading attendance!");
        progressDialog.show();



        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("users");

        String uid=firebaseAuth.getUid();

         databaseReference1= databaseReference1.child(uid);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OverAllAttendance=dataSnapshot.child("Attendance").getValue().toString();
                OverAllAttendanceTextView.setText(OverAllAttendance+"%");
                progressDialog.dismiss();
                LoadDetails();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    int day,month,year;

    public void LoadDetails()
    {
        progressDialog.setMessage("loading records!");
        progressDialog.show();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Attendance Records");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {

                    for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren())
                    {

                        for(DataSnapshot dataSnapshot3:dataSnapshot2.getChildren())
                        {

                            for(DataSnapshot dataSnapshot4:dataSnapshot3.getChildren())
                            {
                                String MeetingName=dataSnapshot4.getKey();
                               // String DOJ=String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
                                String date=dataSnapshot1.getKey()+"/"+dataSnapshot2.getKey()+"/"+dataSnapshot3.getKey();
                               // Toast.makeText(getApplicationContext(),date,Toast.LENGTH_SHORT).show();
                             /*   Date date1 = null;
                                try {
                                    date1 = new SimpleDateFormat("dd/MM/yyyy").parse(DOJ);
                                  //  Toast.makeText(getApplicationContext(),String.valueOf(date1),Toast.LENGTH_SHORT).show();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date date2 = null ;
                                try {
                                date2 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                                   // Toast.makeText(getApplicationContext(),String.valueOf(date2),Toast.LENGTH_SHORT).show();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }*/

                            for(DataSnapshot dataSnapshot5:dataSnapshot4.getChildren())
                            {
                                if(dataSnapshot5.getKey().equals(firebaseAuth.getUid()))
                                {
                                    String name=dataSnapshot4.child(Uid).child("name").getValue().toString();

                                    String status=dataSnapshot4.child(Uid).child("status").getValue().toString();
                                    individualAttendances.add(new IndividualAttendance(MeetingName,name,status,date));
                                }
                            }

                               /*if(date1.compareTo(date2) <= 0)
                                {
                                  //  Toast.makeText(getApplicationContext(),"inside",Toast.LENGTH_SHORT).show();

                                }*/





                            }

                        }



                    }



                }
                progressDialog.dismiss();
                Collections.reverse(individualAttendances);
                arrayAdapter.notifyDataSetChanged();
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
