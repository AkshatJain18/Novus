package com.org.novus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AddAnnouncements extends AppCompatActivity {
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 234;
    ProgressDialog progressDialog;
    EditText AnnouncementContent,AnnouncementTitle,flname;
    AlertDialog.Builder alertDialog;
    String extension;
    String id;
    Spinner DepartmentSpinner;
    private StorageReference mStorageRef;
    String url;
    int CODE=215;

     Uri downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcements);
        AnnouncementContent=(EditText) findViewById(R.id.AnnouncementContent);
        AnnouncementTitle=(EditText) findViewById(R.id.AnnouncementTitle);
        //flname=(TextView)findViewById(R.id.path);
        mStorageRef = FirebaseStorage.getInstance().getReference("attachments/");
        progressDialog=new ProgressDialog(this);
        alertDialog=new AlertDialog.Builder(this);
        addItemsOnSpinner2();
        addListenerOnSpinnerItemSelection();

    }

    //method to show file chooser
    public void showFileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select document"),CODE);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            //Toast.makeText(AddAnnouncements.this,filePath.toString(),Toast.LENGTH_LONG).show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
               // flname.setText(filePath.toString());
                RealPath realPath=new RealPath();
                DepartmentSpinner.setVisibility(View.VISIBLE);

                //AddFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void AddFile()
    {

       final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading file!");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        final AlertDialog.Builder alert=new AlertDialog.Builder(this);
        final StorageReference riversRef = mStorageRef.child(filePath.getLastPathSegment());

        riversRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                 url=uri.toString();


                                progressDialog.dismiss();

                                alert.setMessage("File uploaded!");
                                alert.show();
                                try {
                                    AddAnnouncement();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                // downloadFile(AddAnnouncements.this,filePath.getLastPathSegment(),".pdf",DOWNLOAD_SERVICE,url);
                            }
                        }) ;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }



    int count=0;
    public void getID(View view)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("announcements");
         id=databaseReference.push().getKey();
        try {
            Validate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addListenerOnSpinnerItemSelection() {
        DepartmentSpinner = (Spinner) findViewById(R.id.ExtenisonChooserSpinner);
        DepartmentSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner2() {

        DepartmentSpinner = (Spinner) findViewById(R.id.ExtenisonChooserSpinner);
        List<String> list = new ArrayList<String>();
        list.add("select file extension");
        list.add(".pdf");
        list.add(".png");
        list.add(".jpeg");
        list.add(".ppt");
        list.add(".zip");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DepartmentSpinner.setAdapter(dataAdapter);
    }
    public void Validate() throws IOException {
        Validation validation=new Validation();
        if(validation.CheckEmpty(AnnouncementTitle).equals("Empty"))
        {
            AnnouncementTitle.requestFocus();
            AnnouncementTitle.setError("Please enter a Title");
        }
        else if(validation.CheckEmpty(AnnouncementContent).equals("Empty"))
        {
            AnnouncementContent.requestFocus();
            AnnouncementContent.setError("Please enter description");
        }
        else
        {
            if(filePath!=null)
            {
                if(DepartmentSpinner.getSelectedItemPosition()==0)
                {
                    alertDialog.setMessage("Please select an extension");
                    alertDialog.show();
                }
                else {
                    AddFile();
                }
            }
            else
            {
                AddAnnouncement();
            }

        }
    }
   /* public void SendNotification()
    {

    }*/
    public void AddAnnouncement() throws IOException {
       // AddFile();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        progressDialog.setMessage("Adding announcement!");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String IdeaContentdata=AnnouncementContent.getText().toString();
        String IdeaTitledata=AnnouncementTitle.getText().toString();

         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("announcements").child(id).child("Announcement Title").setValue(IdeaTitledata);
        databaseReference.child("announcements").child(id).child("Announcement Description").setValue(IdeaContentdata);
        databaseReference.child("announcements").child(id).child("Time").setValue(formattedDate);
        databaseReference.child("announcements").child(id).child("SeenBy").push().setValue(new SeenBy("NA","NA"));


        extension=String.valueOf(DepartmentSpinner.getSelectedItem());
        if(DepartmentSpinner.getSelectedItemPosition()==0 && DepartmentSpinner.getVisibility()==View.VISIBLE)
        {
            alertDialog.setMessage("Please select an extension");
            alertDialog.show();
        }
        else {
            if (filePath != null) {

                databaseReference.child("announcements").child(id).child("extension").setValue(extension);
                databaseReference.child("announcements").child(id).child("URL").setValue(url);
                databaseReference.child("announcements").child(id).child("File").setValue(filePath.getLastPathSegment());
            } else {
                databaseReference.child("announcements").child(id).child("extension").setValue("No extension");
                databaseReference.child("announcements").child(id).child("URL").setValue("No URL");
                databaseReference.child("announcements").child(id).child("File").setValue("No File");
            }
        }
        id=id+1;

        databaseReference.child("Last ID").child("Announcement").setValue(id);
        progressDialog.dismiss();
        alertDialog.setMessage("Announcement added Succesfully!");
        alertDialog.show();

    }

   /* public static void sendNotificationToUser(String user, final String message) {
        Firebase ref = new Firebase(FIREBASE_URL);
        final Firebase notifications = ref.child("notificationRequests");

        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);

        notifications.push().setValue(notification);
    }*/
}
