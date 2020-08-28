package com.org.novus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;

public class Content extends AppCompatActivity {

    TextView ContentTextView,ContentTypeTextView,DateTextView,ContentTitleTextView,IdeaGivenBy,FileAttached;
    Button TopIdeaButton;
    String status,File,URL,extension;
    String id;
    ProgressDialog progressBar;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        ContentTextView=(TextView)findViewById(R.id.ContentTextView);
      //  ContentTypeTextView=(TextView)findViewById(R.id.ContentTypeTextView);
        DateTextView=(TextView)findViewById(R.id.Date);
        ContentTitleTextView=(TextView)findViewById(R.id.TitleTextView);
        FileAttached=(TextView)findViewById(R.id.AttachedFileName);
        IdeaGivenBy=(TextView)findViewById(R.id.GivenBy);
        alert=new AlertDialog.Builder(this);
        progressBar=new ProgressDialog(this);
        Intent intent=getIntent();
        String ContentTitle=intent.getStringExtra("ContentTitle");
        String ContentType=intent.getStringExtra("ContentType");
        String Content=intent.getStringExtra("Content");
        id=intent.getStringExtra("idAnnouncement");
        if(ContentType.equals("Idea"))
        {
            IdeaGivenBy.setVisibility(View.VISIBLE);
            String name=intent.getStringExtra("GivenBy");
            IdeaGivenBy.setText(name);
            id=intent.getStringExtra("id");

            TextView Attachments=(TextView)findViewById(R.id.Attachements);
            Attachments.setVisibility(View.GONE);

        }
        else if(ContentType.equals("Announcement")) {
            File = intent.getStringExtra("File");
          //  Toast.makeText(this,File,Toast.LENGTH_SHORT).show();
            URL = intent.getStringExtra("URL");
            extension=intent.getStringExtra("extension");
            if (!File.equals("No File")) {
                FileAttached.setVisibility(View.VISIBLE);
                FileAttached.setText(File +extension);
            }
        }
        String Date=intent.getStringExtra("Date");
        ContentTextView.setText(Content);
        //ContentTypeTextView.setText(ContentType);
        DateTextView.setText(Date);
        ContentTitleTextView.setText(ContentTitle);

        if(ContentType.equals("Announcement")) {
            AddSeen();
        }

    }
    ArrayList<SeenBy> arrayList;
    DatabaseReference databaseReference;
    SeenBy seenBy;
    ArrayList seenBy1;
    String uid;
    public void AddSeen()
    {
          arrayList=new ArrayList<>();
           seenBy1=new ArrayList();
         databaseReference=FirebaseDatabase.getInstance().getReference("announcements");
         uid=FirebaseAuth.getInstance().getUid();
         seenBy=new SeenBy("NA", uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.child(id).child("SeenBy").getChildren())
                {
                    String Uid=dataSnapshot1.child("Uid").getValue().toString();
                    //Toast.makeText(getApplicationContext(),seenBy1.Uid,Toast.LENGTH_SHORT).show();
                    seenBy1.add(Uid);
                }
                if(!seenBy1.contains(uid)) {
                    databaseReference.child(id).child("SeenBy").push().setValue(seenBy);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void download(View view)
    {
        progressBar.setMessage("Downloading File!");
        progressBar.show();
        downloadFile(Content.this,File,extension,DOWNLOAD_SERVICE,URL);

    }
    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url)
    {

        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtension);
        downloadmanager.enqueue(request);
        progressBar.dismiss();
        alert.setMessage("File downloaded successfully");
        alert.show();
    }
    public void onTop(View view)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        databaseReference.child("ideas").child(id).child("Top").setValue("true");

        TopIdeaButton.setText("Selected as Top!");

        TopIdeaButton.setClickable(false);

    }
}
