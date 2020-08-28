package com.org.novus;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.org.novus.RetrieveFirebaseData;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.View;
import android.widget.Button;
import android.app.Fragment;
import android.widget.Toolbar;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.jar.Attributes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class LandingPage extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;
    TextView DOJ;
    String PhotoName;
    private TextView textViewUserEmail;
    int width;

    int height;

    int newWidth = 200;

    int newHeight = 200;

    Matrix matrix;

    Bitmap resizedBitmap;

    float scaleWidth ;
    DatabaseReference mDatabase;
    float scaleHeight;
    FrameLayout frameLayout;
    boolean isAdmin = false;
    Button AddEvents, AddAnnouncements, buttonLogout, AddMeeting, AllIdea;
    AlertDialog.Builder alertDialog;
    RetrieveFirebaseData rd;
    TextView UnSeen;
    ArrayList arrayList1;
    ByteArrayOutputStream outputStream;
    Uri downloadUrl;
    private StorageReference mStorageRef;
    String name, profile_url;
    String Department;
    Menu menu;
    String DateOfJoining;
    String Admin;
    String Attendance;
    String Performance;
    String Incharge;
    String status;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    TextView WelcomeTextView;
    String uid;

    ImageView profile_image;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_performance, menu);
        //MenuItem AllFeedback=(MenuItem)findViewById(R.id.action_all_feedbacks);
        //AllFeedback.setVisible(true);
        this.menu=menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.action_all_feedbacks:
                Intent intent0 = new Intent(getApplicationContext(), AllFeedbacks.class);
                startActivity(intent0);
                break;

            case R.id.GiveFeedback:
                Intent intent = new Intent(getApplicationContext(), GiveFeedback.class);
                startActivity(intent);
                break;

            case R.id.action_logout:
                firebaseAuth.signOut();
                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent1);
                break;

            case R.id.Credits:
                Intent intent2 = new Intent(getApplicationContext(), Credits.class);
                startActivity(intent2);
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing_page2);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.xyz);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        profile_image = (ImageView) findViewById(R.id.profile_image);

        WelcomeTextView = (TextView) findViewById(R.id.WelcomeTextView);

        alertDialog = new AlertDialog.Builder(this);

        AddEvents = (Button) findViewById(R.id.AddEv);

        AddAnnouncements = (Button) findViewById(R.id.AddAnn);

        AddMeeting = (Button) findViewById(R.id.AddMeet);

        AllIdea = (Button) findViewById(R.id.AllIdeas);
        progressDialog = new ProgressDialog(LandingPage.this);
        UnSeen = (TextView) findViewById(R.id.UnseenAnnouncementTextView);
        alertDialog = new AlertDialog.Builder(LandingPage.this);

        firebaseAuth=FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pictures/");
         progressDialog=new ProgressDialog(LandingPage.this);
         progressDialog.setMessage("Please wait");
         progressDialog.setIndeterminate(false);
         progressDialog.setCancelable(false);
        progressDialog.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("announcements")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successfull";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                       // Toast.makeText(LandingPage.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

          arrayList1=new ArrayList();
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Removed Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String check = dataSnapshot1.child("Uid").getValue().toString();
                    arrayList1.add(check);

                    }
                    progressDialog.dismiss();
                Procced();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
                });

    }
    public void Procced()
    {
        FirebaseUser current_user=firebaseAuth.getCurrentUser();


        //  DOJ=(TextView)findViewById(R.id.DOJ);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){

            finish();

            firebaseAuth.signOut();

            startActivity(new Intent(this, LoginActivity.class));
        }


        else if(arrayList1.contains(firebaseAuth.getUid()))
        {
            final ProgressDialog progressDialog=new ProgressDialog(getApplicationContext());
            progressDialog.show();


                current_user.delete()
                        .addOnCompleteListener (new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // alertDialog.setMessage("You are Fired from the organsiation!");
                                    //alertDialog.show();
                                    alertDialog.setMessage("Sorry You are removed from the organsiation!");
                                    alertDialog.show();
                                    progressDialog.dismiss();
                                    firebaseAuth.signOut();
                                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.e("TAG", "User account deletion unsucessful.");
                                }
                            }
                        });


                // databaseReference.child(firebaseAuth.getUid()).removeValue();

        }
        else {

            ShowNoOfAnnouncementsUnSeen();
            RetrieveFirebaseData();
        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        if(firebaseAuth.getCurrentUser() == null){

            finish();

            firebaseAuth.signOut();

            startActivity(new Intent(this, LoginActivity.class));
        }
        ShowNoOfAnnouncementsUnSeen();

    }

    public void ShowNoOfAnnouncementsUnSeen()
    {

        uid=FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("announcements");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int UnSeen1=0;
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    ArrayList arrayList=new ArrayList();

                    for(DataSnapshot dataSnapshot2:dataSnapshot1.child("SeenBy").getChildren())
                    {
                       // Toast.makeText(getApplicationContext(),dataSnapshot2.getKey(),Toast.LENGTH_SHORT).show();
                     String Uid=String.valueOf(dataSnapshot2.child("Uid").getValue());
                     arrayList.add(Uid);
                    }
                    if(!arrayList.contains(uid))
                    {
                        UnSeen1++;
                    }

                }
                UnSeen.setText(String.valueOf(UnSeen1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void display_profile_image() throws IOException, ExecutionException, InterruptedException {

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
        String previouslyEncodedImage = shre.getString("profileImage"+uid, "");
        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
        //    Toast.makeText(getApplicationContext(),"inside shared",Toast.LENGTH_SHORT).show();
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            profile_image.setImageBitmap(bitmap);
            progressDialog.dismiss();
        }
        else {

            progressDialog.dismiss();
            RetrieveFeedTask rd = new RetrieveFeedTask(profile_image, getApplicationContext());
            //Bitmap bitmap=new RetrieveFeedTask().execute(profile_url).get();
            //Toast.makeText(getApplicationContext(),profile_url,Toast.LENGTH_SHORT).show();
            rd.execute(profile_url);
        }
    }

    public void displayInformation()
    {
        //SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
        //String name = shre.getString("name", "");
        //String post = shre.getString("post", "");
        WelcomeTextView.setText(name);
        //Toast.makeText(getApplicationContext(),Admin,Toast.LENGTH_SHORT).show();
        if(Admin.equals("true")) {

            AddAnnouncements.setVisibility(View.VISIBLE);
            AddEvents.setVisibility(View.VISIBLE);
            AllIdea.setVisibility(View.VISIBLE);
            Button AttendanceSystem=findViewById(R.id.AttendanceSystem);
            Button RemoveMember=findViewById(R.id.RemoveMemberButton);
            Button AddMeeting=findViewById(R.id.AddMeet);
            AddMeeting.setVisibility(View.VISIBLE);
            Button Vcode=findViewById(R.id.Vcode);
            Vcode.setVisibility(View.VISIBLE);
            RemoveMember.setVisibility(View.VISIBLE);
            AttendanceSystem.setVisibility(View.VISIBLE);
            MenuItem AllFeedback=menu.findItem(R.id.action_all_feedbacks);
            AllFeedback.setVisible(true);
           // DOJ.setText("Date of joining:"+DateOfJoining);
        }
    }


    public void RetrieveFirebaseData()
    {
       // progressDialog.setIndeterminate(false);
       // progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait!");

        progressDialog.show();
        FirebaseUser user= firebaseAuth.getCurrentUser();



        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        uid=firebaseAuth.getUid();

        mDatabase=mDatabase.child(uid);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                name=dataSnapshot.child("name").getValue().toString();
               // Toast.makeText(LandingPage.this,name,Toast.LENGTH_SHORT).show();
                Admin=dataSnapshot.child("Admin").getValue().toString().trim();
                Department=dataSnapshot.child("Department").getValue().toString();
                DateOfJoining=dataSnapshot.child("DOJ").getValue().toString();
                String skill=dataSnapshot.child("post").getValue().toString();
                TextView textView=(TextView)findViewById(R.id.skill);
                textView.setText(skill);
                Attendance=dataSnapshot.child("Attendance").getValue().toString();
                Performance=dataSnapshot.child("Performance").getValue().toString();
                Incharge=dataSnapshot.child("Incharge").getValue().toString();
                profile_url=String.valueOf(dataSnapshot.child("ProfileUrl").getValue());
               // Toast.makeText(getApplicationContext(),profile_url,Toast.LENGTH_SHORT).show();
               // PhotoName=dataSnapshot.child("users").child(Uid).child("PhotoName").getValue().toString();
                displayInformation();
               try {
                   display_profile_image();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // status="data not rtrved";
                progressDialog.dismiss();
                alertDialog.setMessage(databaseError.toString());
                alertDialog.show();
            }


        });
    }



    public void UploadProfilePicture(View view)
    {
        showFileChooser();

    }
Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 234;
    int CODE=215;
    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"),CODE);
    }
    Bitmap bitmap;
    //handling the image chooser activity result
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
           // Toast.makeText(LandingPage.this,filePath.toString(),Toast.LENGTH_LONG).show();
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Bitmap bitmap1 = bitmap;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                long lengthbmp = imageInByte.length;
                if (lengthbmp > 3000000) {
                    alertDialog.setMessage("Image size should not be more 3MB");
                    alertDialog.setPositiveButton("Ok", null);
                    alertDialog.show();
                }
                else
                {
                   // final ProgressDialog progressDialog=new ProgressDialog(this);
                    progressDialog.setMessage("changing profile picture!");
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(false);
                    progressDialog.show();
                    CompressImage(bitmap);
                    DeleteLastPhoto();
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void CompressImage(Bitmap originalImage)
    {


        width = originalImage.getWidth();

        height = originalImage.getHeight();


        Matrix matrix = new Matrix();

        scaleWidth = ((float) newWidth) / width;

        scaleHeight = ((float) newHeight) / height;

        matrix.postScale(scaleWidth, scaleHeight);


        Bitmap resizedBitmap = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true);

        outputStream = new ByteArrayOutputStream();

        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        width = resizedBitmap.getWidth();


        height = resizedBitmap.getHeight();

        saveToInternalStorage(resizedBitmap);


    }

    public void saveToInternalStorage(Bitmap realImage){
        //  Bitmap realImage = BitmapFactory.decodeStream(stream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        // textEncode.setText(encodedImage);

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit=shre.edit();
        edit.putString("profileImage"+uid,encodedImage);

        edit.apply();

    }

    public void DeleteLastPhoto()
    {
        StorageReference photoRef = null;
        try {
            photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(new URL(profile_url)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                //Log.d(TAG, "onSuccess: deleted file");
                AddFile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                //Log.d(TAG, "onFailure: did not delete file");
                alertDialog.setMessage("Could not change profile picture!");
            }
        });
    }


    String url,ProfileUrl;
    public void AddFile()
    {

        final AlertDialog.Builder alert=new AlertDialog.Builder(this);
        final StorageReference riversRef = mStorageRef.child(filePath.getLastPathSegment());

        riversRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onSuccess(Uri uri) {

                                url=uri.toString();
                                ProfileUrl=url;


                                UpdateFirebase();

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

                        alert.setMessage("Profile picture not uploaded!");
                        alert.show();
                        progressDialog.dismiss();
                    }
                });


    }

    public void UpdateFirebase()
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(uid).child("ProfileUrl").setValue(ProfileUrl);
        profile_url=ProfileUrl;
        profile_image.setImageBitmap(bitmap);
        progressDialog.dismiss();
        alertDialog.setMessage("Profile picture changed!");
        alertDialog.setPositiveButton("Ok",null);
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {

        if(view == buttonLogout){

            firebaseAuth.signOut();

            finish();

            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public void DisplayDepartment(View view)
    {
        Intent intent=new Intent(getApplicationContext(),DepartmentActivity.class);
        intent.putExtra("Department",Department);
        intent.putExtra("Incharge",Incharge);
        startActivity(intent);
    }
    public void SubmitIdeas(View view)
    {

        Intent intent=new Intent(getApplicationContext(),SubmitIdeasActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
    public void DisplayEvents(View view)
    {
        Intent intent=new Intent(getApplicationContext(),Events.class);
        startActivity(intent);
    }

    public void DisplayMeetings(View view)
    {
        Intent intent=new Intent(getApplicationContext(),Meetings.class);
        startActivity(intent);
    }
    public void AddAnnouncements(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AddAnnouncements.class);
        startActivity(intent);
    }

    public void DisplayAnnouncements(View view)
    {
        Intent intent=new Intent(getApplicationContext(),Announcements.class);
        intent.putExtra("type","announcement");
        startActivity(intent);
    }
    public void AddEvent(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AddEvent.class);
        startActivity(intent);
    }
    public void AddMeeting(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AddMeeting.class);
        startActivity(intent);
    }
    public void AllIdeas(View view)
    {
        Intent intent=new Intent(getApplicationContext(),Ideas.class);
        startActivity(intent);
    }
    public void AttendanceSystem(View view)
    {
        Intent intent=new Intent(getApplicationContext(),AttendanceSystem.class);
        startActivity(intent);
    }
    public void DisplayAttendance(View view)
    {
        Intent intent=new Intent(getApplicationContext(),Attendance.class);
        startActivity(intent);
    }
    public void GenerateNewMemberVerificationCode(View view)
    {
        Intent intent=new Intent(getApplicationContext(),NewMemberVerificationCode.class);
        startActivity(intent);
    }
    public void RemoveMember(View view)
    {
        Intent intent=new Intent(getApplicationContext(),RemoveMember.class);
        startActivity(intent);
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;
        private Context context;
        Bitmap bmp;
        //ProgressDialog progressBar;
        ImageView profile_image;
        ProgressDialog progressDialog1;

        public RetrieveFeedTask(ImageView Profile_image,Context context1)
        {
            this.profile_image=Profile_image;
            this.context=context1;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

            progressDialog1=new ProgressDialog(LandingPage.this);
            progressDialog1.setMessage("Loading... Please wait...");
            progressDialog1.setIndeterminate(false);
            progressDialog1.setCancelable(false);
            progressDialog1.show();
        }
        @Override
        protected Bitmap doInBackground(String... urls)  {
            URL url = null;
            try {
                // progressBar=new ProgressDialog(getApplicationContext());
                //progressBar.setMessage("Please wait!");
                //progressBar.show();
                url = new URL(urls[0]);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                //saveToInternalStorage(bmp);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            profile_image.setImageBitmap(bitmap);
            //Toast.makeText(getApplicationContext(),bitmap.toString(),Toast.LENGTH_SHORT).show();
            CompressImage(bitmap);

            // progressDialog.dismiss();
            progressDialog1.dismiss();
        }
    }


}