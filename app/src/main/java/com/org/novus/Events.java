package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Events extends AppCompatActivity {

    ListView EventsListView;
    ArrayAdapter arrayAdapter;
    AlertDialog.Builder alertDialog;
    String id,Admin,Uid;
    int itemPosition;
    ArrayList<MeetingListItemData> EventList;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        EventsListView=(ListView)findViewById(R.id.EventListView);
        EventList=new ArrayList<MeetingListItemData>();
        Uid= FirebaseAuth.getInstance().getUid();
        alertDialog=new AlertDialog.Builder(this);
        arrayAdapter = new ArrayAdapter(this, R.layout.meeting_list_item, R.id.MeetingTitle, EventList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView Title = (TextView) view.findViewById(R.id.MeetingTitle);
                TextView Date = (TextView) view.findViewById(R.id.Date);
                TextView Time = (TextView) view.findViewById(R.id.Time);
                TextView MeetingTitleHeading = (TextView) view.findViewById(R.id.textView17);
                TextView MeetingDateHeading = (TextView) view.findViewById(R.id.textView19);
                TextView MeetingTimeHeading = (TextView) view.findViewById(R.id.textView20);
                TextView MeetingVenue=(TextView)view.findViewById(R.id.VenueTextView);
                MeetingVenue.setText(EventList.get(position).Venue);
                Typeface t1=Typeface.createFromAsset(getAssets(),"Caviar_Dreams_Bold.ttf");
                MeetingDateHeading.setTypeface(t1);
                MeetingTitleHeading.setTypeface(t1);
                MeetingTimeHeading.setTypeface(t1);
                Typeface t=Typeface.createFromAsset(getAssets(),"CaviarDreams.ttf");
                Title.setText(EventList.get(position).Title);
                Title.setTypeface(t);
                Time.setText(EventList.get(position).Time);
                Time.setTypeface(t);
                Date.setText(EventList.get(position).Date);
                Date.setTypeface(t);
                if(Admin.equals("true"))
                {
                    Button btnTag=(Button)view.findViewById(R.id.DeleteButton);
                    btnTag.setVisibility(View.VISIBLE);
                    btnTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemPosition=position;
                            ShowDeleteButton(itemPosition);
                        }
                    });

                }
                return view;
            }
        };


        EventsListView.setAdapter(arrayAdapter);


     ReadAdmin();
    }

    public void ShowDeleteButton(final int position)
    {

                alertDialog.setMessage("are you sure to delete this event?");
                alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("events");
                        databaseReference.child(EventList.get(position).id).removeValue();
                        EventList.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        EventsListView.setAdapter(arrayAdapter);
                        AlertDialog.Builder alert=new AlertDialog.Builder(Events.this);
                        alert.setMessage("Event deleted!");
                        alert.setPositiveButton("Ok",null);
                        alert.show();
                    }
                });

                alertDialog.setNegativeButton("No",null);
                alertDialog.show();

    }



    public void ReadAdmin()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");
        String Uid=FirebaseAuth.getInstance().getUid();
        databaseReference=databaseReference.child(Uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Admin=dataSnapshot.child("Admin").getValue().toString();
                displayEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                AlertDialog.Builder alert=new AlertDialog.Builder(Events.this);
                alert.setMessage(databaseError.toString() + "in Reading Admin");
                alert.setPositiveButton("Ok",null);
                alert.show();
            }
        });

    }
    public void displayEvents()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("events");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String Title=dataSnapshot1.child("Event Name").getValue().toString();
                    //Toast.makeText(getApplicationContext(),Title,Toast.LENGTH_SHORT).show();
                    String Date=dataSnapshot1.child("Event Date").getValue().toString();
                    String Time=dataSnapshot1.child("Event Time").getValue().toString();
                    String Venue=dataSnapshot1.child("Event Venue").getValue().toString();
                    String id=dataSnapshot1.getKey();
                    EventList.add(new MeetingListItemData(Title,Date,Time,Venue,id));

                }
                Collections.reverse(EventList);
                arrayAdapter.notifyDataSetChanged();
                EventsListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                AlertDialog.Builder alert=new AlertDialog.Builder(Events.this);
                alert.setMessage(databaseError.toString()+" in display events");
                alert.setPositiveButton("Ok",null);
                alert.show();
            }
        });
    }

    public void setStyle(TextView textView, String style)
    {
        Typeface t1=Typeface.createFromAsset(getAssets(),"Caviar_Dreams_Bold.ttf");
        textView.setTypeface(t1);
    }

}
