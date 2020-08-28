package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Meetings extends AppCompatActivity {

    ListView listView;
    String type,Admin;

    ArrayList<MeetingListItemData> announcements;
    ArrayAdapter<String> arrayAdapter;
    SharedPreferences ann, IdeaSharedPreferences;
    AlertDialog.Builder alertDialog;
    int total, total1;
    int itemPosition;
    String itemValue;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);
        listView = (ListView) findViewById(R.id.MeetingListView);

        announcements = new ArrayList<MeetingListItemData>();


       alertDialog=new AlertDialog.Builder(this);
        arrayAdapter = new ArrayAdapter(this, R.layout.meeting_list_item, R.id.MeetingTitle, announcements) {
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
                MeetingVenue.setText(announcements.get(position).Venue);
                Typeface t1=Typeface.createFromAsset(getAssets(),"Caviar_Dreams_Bold.ttf");
                MeetingDateHeading.setTypeface(t1);
                MeetingTitleHeading.setTypeface(t1);
                MeetingTimeHeading.setTypeface(t1);
                Typeface t=Typeface.createFromAsset(getAssets(),"CaviarDreams.ttf");
                Title.setText(announcements.get(position).Title);
                Title.setTypeface(t);
                Time.setText(announcements.get(position).Time);
                Time.setTypeface(t);
                Date.setText(announcements.get(position).Date);
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


        listView.setAdapter(arrayAdapter);

        RetreiveName();


    }


    public void ShowDeleteButton(final int position)
    {

        alertDialog.setMessage("are you sure to delete this meeting?");
        alertDialog.setNegativeButton("No",null);
        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("meetings");
                databaseReference.child(announcements.get(position).id).removeValue();
                announcements.remove(position);
                arrayAdapter.notifyDataSetChanged();
                listView.setAdapter(arrayAdapter);
                AlertDialog.Builder alert=new AlertDialog.Builder(Meetings.this);
                alert.setMessage("Meeting deleted!");
                alert.setPositiveButton("Ok",null);
                alert.show();
            }
        });


        alertDialog.show();

    }

    public void RetreiveName()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        String uid=FirebaseAuth.getInstance().getUid();
        databaseReference=databaseReference.child(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Admin=dataSnapshot.child("Admin").getValue().toString();
                DisplayAnnouncementTitles();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void DisplayAnnouncementTitles() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("meetings");



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {


                String MeetingTitle = dataSnapshot1.child("Meeting Name").getValue().toString();

                String Time = dataSnapshot1.child("Meeting Time").getValue().toString();

                String Date = dataSnapshot1.child("Meeting Date").getValue().toString();

                String Venue=dataSnapshot1.child("Meeting Venue").getValue().toString();


                String id=dataSnapshot1.getKey();
                announcements.add(new MeetingListItemData(MeetingTitle,Date,Time,Venue,id));


                }
                Collections.reverse(announcements);
                arrayAdapter.notifyDataSetChanged();
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }


    public void blink()
    {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        listView.startAnimation(anim);
    }
}

