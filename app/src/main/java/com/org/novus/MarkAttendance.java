package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class MarkAttendance extends AppCompatActivity {

    ListView AttendanceListView;
     String status;
    ArrayAdapter<AttendanceData> arrayAdapter;
    ArrayList<AttendanceData> members;
    AlertDialog.Builder alertDialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    DatabaseReference databaseReference;
    Spinner TimeSpinner;
    String date;
    List<String> list;
    AlertDialog.Builder alertDialog1;
    ProgressDialog progressDialog;
    EditText DateEditText,TimeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        AttendanceListView=(ListView)findViewById(R.id.AttendanceListView);

        DateEditText=(EditText)findViewById(R.id.DateEditText);


        members=new ArrayList<>();

        progressDialog=new ProgressDialog(this);

        progressDialog.setCancelable(false);

        progressDialog.setIndeterminate(false);

        alertDialog=new AlertDialog.Builder(this);
        TimeSpinner=(Spinner)findViewById(R.id.TimeSpinner);

        addListenerOnSpinnerItemSelection();
        addItemsOnSpinner2();

        LoadMembers();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                 date = Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
                DateEditText.setText(date);
                AttendanceListView.requestFocus();
            }
        };



        arrayAdapter=new ArrayAdapter(this,R.layout.mark_attendance_list_item,R.id.MemberName,members)
        {
            Spinner AttendanceSpinner;
            String SpinnerValue;
            @Override
            public View getView(final int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);

                TextView MemberName=(TextView)view.findViewById(R.id.MemberName);

                 AttendanceSpinner=(Spinner)view.findViewById(R.id.AttendanceSpinner);

                addItemsOnSpinner2();

               // addListenerOnSpinnerItemSelection();

                MemberName.setText(members.get(position).name);

                 //SpinnerValue=String.valueOf(AttendanceSpinner.getSelectedItem());

                // members.get(position).AttendanceStatus=SpinnerValue;

                AttendanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        SpinnerValue=adapterView.getItemAtPosition(i).toString();

                        //Toast.makeText(getApplicationContext(),adapterView.getItemAtPosition(i).toString(),Toast.LENGTH_SHORT).show();
                        members.get(position).AttendanceStatus=SpinnerValue;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                return view;
            }
            public void addListenerOnSpinnerItemSelection() {

                //AttendanceSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
            }

            // add items into spinner dynamically
            public void addItemsOnSpinner2() {


                List<String> list = new ArrayList<String>();
                list.add("Present");
                list.add("Absent");
                list.add("Not Called");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                AttendanceSpinner.setAdapter(dataAdapter);
            }

        };
        AttendanceListView.setAdapter(arrayAdapter);
    }
    public void addListenerOnSpinnerItemSelection() {

        TimeSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addItemsOnSpinner2() {
        progressDialog.setMessage("Fetching Meetings!");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(false);
          list = new ArrayList<String>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("meetings");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String Meeeting=dataSnapshot1.child("Meeting Name").getValue().toString();
                    list.add(Meeeting);
                }
                progressDialog.dismiss();
                Collections.reverse(list);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                TimeSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void LoadMembers()
    {
        progressDialog.setMessage("Loading Members!");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    progressDialog.dismiss();
                    String name=dataSnapshot1.child("name").getValue().toString();
                    String total_attended=dataSnapshot1.child("Total_Attended").getValue().toString();
                    String total_meetings=dataSnapshot1.child("Total_Meetings").getValue().toString();
                    String Uid=dataSnapshot1.getKey();
                    members.add(new AttendanceData(name,Uid,"NA",total_attended,total_meetings));
                }
                arrayAdapter.notifyDataSetChanged();
                AttendanceListView.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onDateClick(View view)
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                MarkAttendance.this,
                android.R.style.Widget_DatePicker,
                mDateSetListener,
                year,month,day);
        dialog.setTitle("Select Date");
        dialog.show();
    }

    public void Confirm()
    {

        alertDialog.setMessage("are you sure?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {

                    if(DateEditText.getText().toString().equals("") || TimeSpinner.getSelectedItem()==null)
                    {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MarkAttendance.this);
                        alertDialog.setMessage("please fill all the fields");
                        alertDialog.setPositiveButton("Ok",null);
                        alertDialog.show();
                    }
                    else {
                        MarkAttendance();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }
    float total;
    String meetingName;
    public void MarkAttendance() throws ParseException {
        progressDialog.setMessage("Submitting Attendance! Please Wait");
        progressDialog.show();

        final String date = DateEditText.getText().toString();
        alertDialog1=new AlertDialog.Builder(MarkAttendance.this);

       // Toast.makeText(getApplicationContext(),"Date: "+date,Toast.LENGTH_SHORT).show();
         meetingName = String.valueOf(TimeSpinner.getSelectedItem());

        if(meetingName.equals("")) {
            alertDialog.setMessage("Please select a meeting!");
            alertDialog.setPositiveButton("Ok",null);
            alertDialog.show();
        }
        else if(date.equals(""))
        {
            alertDialog.setMessage("Please select a date!");
            alertDialog.setPositiveButton("Ok",null);
            alertDialog.show();
        }
        else {
             DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Attendance Records");
             databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                         if(dataSnapshot.child(date).child(meetingName).exists())
                         {
                             progressDialog.dismiss();

                             alertDialog1.setMessage("Attendance is already marked for this meeting");
                             alertDialog1.setPositiveButton("Ok",null);
                             alertDialog1.show();

                         }
                         else
                         {
                             final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                             for (AttendanceData data : members) {
                                 databaseReference.child("Attendance Records").child(date).child(meetingName).child(data.Uid).child("name").setValue(data.name);
                                 databaseReference.child("Attendance Records").child(date).child(meetingName).child(data.Uid).child("status").setValue(data.AttendanceStatus);
                             }
                             progressDialog.dismiss();
                             alertDialog.setMessage("Attendance Marked!");
                             alertDialog.setPositiveButton("Ok", null);
                             alertDialog.setNegativeButton(null, null);
                             alertDialog.show();


                             SaveIndividualAttendances();
                         }
                     }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });

        }




    }
    AttendanceData data;
    public void SaveIndividualAttendances()
    {
        //AttendanceData data;

       progressDialog.setMessage("Saving Members Attendance");
       progressDialog.setIndeterminate(false);
       progressDialog.setCancelable(false);

       for(int i=0;i<members.size();i++)
       {
           AttendanceData data=members.get(i);
           String TotalAttended=data.Total_Attended;
           int Total_Attended=Integer.parseInt(TotalAttended);
           String TotalMeetings=data.Total_Meetings;
           int total_meetings=Integer.valueOf(TotalMeetings);
          // Toast.makeText(getApplicationContext(),Total_Attended,Toast.LENGTH_SHORT).show();
           //total=total+1;
           DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference();

           if(data.AttendanceStatus.equals("Present"))
           {
               Total_Attended++;
               total_meetings++;
               data.Total_Attended=String.valueOf(Total_Attended);
               data.Total_Meetings=String.valueOf(total_meetings);
              //Toast.makeText(getApplicationContext(),Total_Attended,Toast.LENGTH_SHORT).show();
             //  Toast.makeText(getApplicationContext(),total_meetings,Toast.LENGTH_SHORT).show();
               databaseReference1.child("users").child(data.Uid).child("Total_Meetings").setValue(String.valueOf(total_meetings));
               DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");
               databaseReference.child(data.Uid).child("Total_Attended").setValue(Total_Attended);
               int attendance=(int)((Float.valueOf(Total_Attended)/Float.valueOf(total_meetings))*100);
               databaseReference.child(data.Uid).child("Attendance").setValue(String.valueOf(attendance));
              // Toast.makeText(getApplicationContext(),String.valueOf(attendance),Toast.LENGTH_SHORT).show();
           }
           else if(data.AttendanceStatus.equals("Not Called"))
           {
                 //do nothing
           }
           else
           {
               total_meetings++;
               databaseReference1.child("users").child(data.Uid).child("Total_Meetings").setValue(total_meetings);
               int attendance=(int)((Float.valueOf(Total_Attended)/Float.valueOf(total_meetings))*100);
               databaseReference1.child("users").child(data.Uid).child("Attendance").setValue(String.valueOf(attendance));
           }


       }
       progressDialog.dismiss();

    }

    public void OnSubmit(View view)
    {


        if(DateEditText.getText().toString().equals(""))
        {
            progressDialog.dismiss();
            alertDialog.setMessage("Please Enter all the details");
            alertDialog.setPositiveButton("OK",null);
            alertDialog.show();
        }
        else {
            progressDialog.dismiss();
            Confirm();
        }
    }


}
