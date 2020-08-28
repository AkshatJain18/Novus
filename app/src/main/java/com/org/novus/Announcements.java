package com.org.novus;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Announcements extends AppCompatActivity {

    ListView listView;
    String type;
    ArrayList<AnnouncementItemData> announcements;
    ArrayList<String> TitlesAnnouncement;
    ArrayList<AnnouncementItemData> all;
    ArrayAdapter<String> arrayAdapter;
    SharedPreferences ann, IdeaSharedPreferences;
    ProgressDialog progressDialog;
    SearchView searchView;
    int total, total1;
    int itemPosition;
    String itemValue;
    boolean flag=false;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);
        listView = (ListView) findViewById(R.id.announcementlistview);
         progressDialog=new ProgressDialog(this);
         progressDialog.setMessage("Loading..");
        announcements = new ArrayList<AnnouncementItemData>();
        all=new ArrayList<AnnouncementItemData>();
        searchView=(SearchView)findViewById(R.id.SearchAnnouncementBox);
        TitlesAnnouncement=new ArrayList<>();
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.setIconified(true);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {



                    for(int i=0;i<announcements.size();i++)
                    {
                        if(announcements.get(i).Title.contains(s))
                        {
                            AnnouncementItemData announcementItemData=announcements.get(i);
                            announcements.add(announcementItemData);
                            flag=true;
                        }

                    }
                    arrayAdapter.notifyDataSetChanged();
                    listView.setAdapter(arrayAdapter);

                if (flag==false){
                    Toast.makeText(Announcements.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        arrayAdapter = new ArrayAdapter(this, R.layout.listrow, R.id.HeadingTextView, announcements) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView Heading = (TextView) view.findViewById(R.id.HeadingTextView);
                TextView Time = (TextView) view.findViewById(R.id.DateTextView);
                TextView New = (TextView) view.findViewById(R.id.NewTextView);
                TextView ShortDescription = (TextView) view.findViewById(R.id.ShortDescriptionTextView);

                Heading.setText(announcements.get(position).Title);
                Time.setText(announcements.get(position).Time);
                if(announcements.get(position).Description.length()>20) {

                    ShortDescription.setText(announcements.get(position).Description.substring(0, 20)+"...");
                }
                else
                {
                    ShortDescription.setText(announcements.get(position).Description);
                }

                Button ViewButton;

                ViewButton=(Button)view.findViewById(R.id.ViewButton);

                ArrayList<SeenBy> arrayList =new ArrayList<>();

                arrayList=announcements.get(position).SeenBy;

                boolean flag=false;
                for(SeenBy seenBy:arrayList)
                {
                    if(seenBy.Uid.equals(FirebaseAuth.getInstance().getUid()))
                    {
                        flag=true;
                        break;
                    }
                }
                if(flag==false)
                {
                    New.setVisibility(View.VISIBLE);
                }

                ViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AnnouncementItemData ih=announcements.get(position);

                        Intent intent = new Intent(getApplicationContext(), Content.class);
                        intent.putExtra("ContentType", "Announcement");
                        intent.putExtra("ContentTitle", ih.Title);
                        intent.putExtra("Content", ih.Description);
                        intent.putExtra("Date", ih.Time);
                        intent.putExtra("File",ih.File);
                        intent.putExtra("URL",ih.URL);
                        intent.putExtra("idAnnouncement",ih.id);
                        intent.putExtra("extension",ih.extension);
                        startActivity(intent);
                    }
                });
                return view;
            }
        };


        listView.setAdapter(arrayAdapter);



        displayAnnouncements10();



    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_in_left);
    }
    int count1=1;
    public void displayAnnouncements10()
    {
        progressDialog.show();
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("announcements");
       // databaseReference.child().orderByChild("Time");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                announcements.clear();
                TitlesAnnouncement.clear();

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                        String id = dataSnapshot1.getKey();
                        String Description = String.valueOf(dataSnapshot1.child("Announcement Description").getValue());
                        String Title = String.valueOf(dataSnapshot1.child("Announcement Title").getValue());
                        // Toast.makeText(getApplicationContext(),Title,Toast.LENGTH_SHORT).show();
                        String Time = String.valueOf(dataSnapshot1.child("Time").getValue());
                        String File = String.valueOf(dataSnapshot1.child("File").getValue());
                        String URL = String.valueOf(dataSnapshot1.child("URL").getValue());
                        TitlesAnnouncement.add(Title);
                        String extension = String.valueOf(dataSnapshot1.child("extension").getValue());
                        ArrayList<SeenBy> arrayList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Seen By").getChildren()) {

                            SeenBy seenBy = new SeenBy("NA", String.valueOf(dataSnapshot2.child("Uid").getValue()));
                            arrayList.add(seenBy);
                        }
                        count1++;
                        announcements.add(new AnnouncementItemData(Title, Time, Description, File, URL, extension, arrayList, id));

                }
                progressDialog.dismiss();
                Collections.reverse(announcements);
                arrayAdapter.notifyDataSetChanged();
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });

    }
   /* public void displayAllAnnouncements(View view)
    {
        searchView.setVisibility(View.VISIBLE);
        progressDialog.show();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("announcements");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                announcements.clear();
                TitlesAnnouncement.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String id=dataSnapshot1.getKey();
                    String Description=String.valueOf(dataSnapshot1.child("Announcement Description").getValue());
                    String Title=String.valueOf(dataSnapshot1.child("Announcement Title").getValue());
                   // Toast.makeText(getApplicationContext(),Title,Toast.LENGTH_SHORT).show();
                    String Time=String.valueOf(dataSnapshot1.child("Time").getValue());
                    String File=String.valueOf(dataSnapshot1.child("File").getValue());
                    String URL=String.valueOf(dataSnapshot1.child("URL").getValue());
                    TitlesAnnouncement.add(Title);
                    String extension=String.valueOf(dataSnapshot1.child("extension").getValue());
                    ArrayList<SeenBy> arrayList=new ArrayList<>();
                    for(DataSnapshot dataSnapshot2:dataSnapshot1.child("Seen By").getChildren())
                    {

                        SeenBy seenBy=new SeenBy("NA",dataSnapshot2.child("Uid").getValue().toString());
                        arrayList.add(seenBy);
                    }
                    announcements.add(new AnnouncementItemData(Title,Time,Description,File,URL,extension,arrayList,id));

                }

                progressDialog.dismiss();
                Collections.reverse(announcements);
                arrayAdapter.notifyDataSetChanged();
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/


    public void blink(TextView textView)
    {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(anim);
    }
}

