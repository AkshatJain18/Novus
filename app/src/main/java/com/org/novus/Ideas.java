package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ideas extends AppCompatActivity {

    ListView listView;
    TextView ViewTextView;
    CheckBox TopCheckBox;
    TextView txt;
    String type;
    ArrayList<IdeaHeading> ideas;
    ArrayAdapter arrayAdapter;
    SharedPreferences IdeaSharedPreferences;
    int total, total1;
    int itemPosition;
    String itemValue;
    SearchView searchView;
    Map<String, String> LikedBy;
    ArrayList arrayList;
    String name;
    ArrayList<IdeaHeading> ideas1;

    int count=0,id;

    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ideas);
        listView = (ListView) findViewById(R.id.IdeasListView);

        ideas = new ArrayList<IdeaHeading>();

       searchView=(SearchView)findViewById(R.id.searchView);


        listView.setFocusable(true);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.setIconified(true);
                }
            }
        });
        ideas1=new ArrayList<>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                ideas1.clear();

                    for(int i=0;i<ideas.size();i++)
                    {
                        if(ideas.get(i).Heading.contains(s))
                        {

                            IdeaHeading announcementItemData=ideas.get(i);
                            ideas1.add(announcementItemData);
                            flag=true;
                        }

                    }
                    if(flag=true) {
                        ideas.clear();
                        ideas = ideas1;
                        arrayAdapter.notifyDataSetChanged();
                        listView.setAdapter(arrayAdapter);
                    }

                else {
                    Toast.makeText(Ideas.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        txt=(TextView)findViewById(R.id.DateTextView);

         arrayAdapter = new ArrayAdapter(this, R.layout.idea_list_item, R.id.HeadingIdeaTextView, ideas) {
             Button LikeButton;
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView Heading = (TextView) view.findViewById(R.id.HeadingIdeaTextView);

                TextView ShortDescription = (TextView) view.findViewById(R.id.ShortDescriptionTextView);

                if(ideas.get(position).Description.length()>20) {

                    ShortDescription.setText(ideas.get(position).Description.substring(0, 20)+"...");
                }
                else
                {
                    ShortDescription.setText(ideas.get(position).Description);
                }

                TextView Time = (TextView) view.findViewById(R.id.DateIdeaTextView);

                RelativeLayout item=(RelativeLayout)findViewById(R.id.item);

                Heading.setText(ideas.get(position).Heading);

                Time.setText(ideas.get(position).Time);

                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                LikeButton=(Button)view.findViewById(R.id.LikeButton);
                LikeButton.setText(String.valueOf(ideas.get(position).Likes));
                final String Uid=firebaseAuth.getUid();
                if(ideas.get(position).LikedByKeysList.contains(Uid)) {
                    LikeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //ideas.get(position).Likes=ideas.get(position).Likes+1;
                            //LikeButton.setText(String.valueOf(ideas.get(position).Likes));
                           // DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("ideas");
                            //databaseReference.child(ideas.get(position).ID).child("Likes").setValue(String.valueOf(ideas.get(position).Likes));

                            Toast.makeText(getApplicationContext(),"Idea already Liked!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {


                    LikeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LikeButton.setClickable(false);
                            int i=ideas.get(position).Likes;
                            i=i+1;
                           // Toast.makeText(getApplicationContext(),"Idea Liked!",Toast.LENGTH_SHORT).show();
                            ShowAlert();
                            LikeButton.setText(String.valueOf(i));
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ideas");
                            databaseReference.child(ideas.get(position).ID).child(ideas.get(position).pushId).child("Likes").setValue(String.valueOf(i));
                            databaseReference.child(ideas.get(position).ID).child(ideas.get(position).pushId).child("Liked By").child(Uid).setValue(name);


                        }
                    });
                }

                //TopCheckBox=(CheckBox)view.findViewById(R.id.TopIdeaCheckBox);

                //Toast.makeText(Ideas.this,Integer.toString(position)+": "+ideas.get(position).CheckBoxStatus,Toast.LENGTH_SHORT).show();

                Button ViewButton;

                ViewButton=(Button)view.findViewById(R.id.ViewButton);




                ViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemPosition = position;
                        IdeaHeading ih=(IdeaHeading) listView.getItemAtPosition(position);

                        itemValue=ih.getHeading();

                        SendData();
                    }
                });

                return view;
            }


        };

        listView.setAdapter(arrayAdapter);





        DisplayAnnouncementTitles();
    }

    public void ShowAlert()
    {
        AlertDialog.Builder alert=new AlertDialog.Builder(Ideas.this);
        alert.setMessage("Idea Liked!");
        alert.setPositiveButton("Ok",null);
        alert.show();
    }
    public void SendData()
    {
                Intent intent = new Intent(Ideas.this, Content.class);
                intent.putExtra("ContentType", "Idea");
                intent.putExtra("ContentTitle",ideas.get(itemPosition).Heading );
                intent.putExtra("Content", ideas.get(itemPosition).Description);
                intent.putExtra("Date", ideas.get(itemPosition).Time);
                intent.putExtra("GivenBy",ideas.get(itemPosition).Givenby);
                intent.putExtra("Top",ideas.get(itemPosition).CheckBoxStatus);
                intent.putExtra("id",ideas.get(itemPosition).ID);
                startActivity(intent);
    }
int count1=0;
    public void DisplayAnnouncementTitles() {


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 //LikedBy= (Map<String, String>) dataSnapshot.child("ideas").child("Liked By").getValue();
                 //arrayList=new ArrayList();

                //Log.i("HashMap",LikedBy.toString());
                //Log.i("msg",arrayList);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String Uid = firebaseAuth.getCurrentUser().getUid();
                name = dataSnapshot.child(Uid).child("name").getValue().toString();
                Display();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
        public void Display()
        {
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Loading Ideas!");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ideas");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id1 = Integer.toString(id);
                String IdeaTitle;

                ideas.clear();


                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {

                        String id = dataSnapshot1.getKey();

                     for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()) {

                         String pushId=dataSnapshot2.getKey();

                         IdeaTitle = dataSnapshot2.child("Idea Title").getValue().toString();

                         String Time = dataSnapshot2.child("Time").getValue().toString();

                         String Description = dataSnapshot2.child("Idea Description").getValue().toString();

                         String IdeaGivenby = dataSnapshot2.child("Idea given by").getValue().toString();

                         String CheckBoxStatus = dataSnapshot2.child("Top").getValue().toString();
                         arrayList = new ArrayList();

                         for (DataSnapshot dataSnapshot3 : dataSnapshot2.child("Liked By").getChildren()) {
                             String Uid = dataSnapshot3.getKey();
                             //Toast.makeText(getApplicationContext(),Uid,Toast.LENGTH_SHORT).show();
                             String name = dataSnapshot3.child(Uid).toString();
                             arrayList.add(Uid);
                         }

                         //LikedBy= (Map<String, String>) dataSnapshot1.child("Liked By").getValue();

                         //Log.i("msg",arrayList.get(0).toString());

                         int likes = Integer.valueOf(dataSnapshot2.child("Likes").getValue().toString());


                         IdeaHeading ih = new IdeaHeading(IdeaTitle, Time, Description, IdeaGivenby, CheckBoxStatus, id, likes, arrayList,pushId);


                         ideas.add(ih);

                     }



                }
                Collections.reverse(ideas);
                arrayAdapter.notifyDataSetChanged();

                listView.setAdapter(arrayAdapter);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }
  /*  public void Display()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading Ideas!");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ideas");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id1 = Integer.toString(id);
                String IdeaTitle;

                ideas.clear();


                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {


                        String id = dataSnapshot1.getKey();

                        IdeaTitle = dataSnapshot1.child("Idea Title").getValue().toString();

                        String Time = dataSnapshot1.child("Time").getValue().toString();

                        String Description = dataSnapshot1.child("Idea Description").getValue().toString();

                        String IdeaGivenby = dataSnapshot1.child("Idea given by").getValue().toString();

                        String CheckBoxStatus = dataSnapshot1.child("Top").getValue().toString();
                        arrayList = new ArrayList();

                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("Liked By").getChildren()) {
                            String Uid = dataSnapshot2.getKey();
                            //Toast.makeText(getApplicationContext(),Uid,Toast.LENGTH_SHORT).show();
                            String name = dataSnapshot2.child(Uid).toString();
                            arrayList.add(Uid);
                        }

                        //LikedBy= (Map<String, String>) dataSnapshot1.child("Liked By").getValue();

                        //Log.i("msg",arrayList.get(0).toString());

                        int likes = Integer.valueOf(dataSnapshot1.child("Likes").getValue().toString());


                        IdeaHeading ih = new IdeaHeading(IdeaTitle, Time, Description, IdeaGivenby, CheckBoxStatus, id, likes, arrayList);


                        ideas.add(ih);




                }
                Collections.reverse(ideas);
                arrayAdapter.notifyDataSetChanged();

                listView.setAdapter(arrayAdapter);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }*/

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_in_left);
    }
    public void blink()
    {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
       // ListView.startAnimation(anim);
    }
}
