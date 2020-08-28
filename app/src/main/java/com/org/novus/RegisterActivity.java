package com.org.novus;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String Uid;
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 234;
    String Roles;
    String DateofJoining;
    String status,post;
    EditText Vcode,Post;
    Bitmap bitmap;
    ImageView im;

    Bitmap originalImage;

    int width;

    int height;

    int newWidth = 200;

    int newHeight = 200;

    Matrix matrix;

    Bitmap resizedBitmap;

    float scaleWidth ;

    float scaleHeight;

    ByteArrayOutputStream outputStream;
    String VcodeNo;
    Spinner DepartmentSpinner;
    String DepartmentName;
    EditText EmailIdBlock,passwordBlock,NameBlock,DOJ;
    String Email,password,FullName;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    int PERMISSION_READ_EXTERNAL_STORAGE=1;
    String ProfileUrl=null;
    FirebaseUser current_user;
    String url;
    boolean flag=false;
    int CODE=215;
    Uri downloadUrl;
    private StorageReference mStorageRef;
    ImageView UploadView;
    AlertDialog.Builder alertDialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Date DeadlineDate,CurrentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UploadView=(ImageView)findViewById(R.id.UploadView);
        EmailIdBlock=(EditText)findViewById(R.id.emailIdBlock);
        passwordBlock=(EditText)findViewById(R.id.passwordBlock);
        Post=(EditText)findViewById(R.id.post);
        NameBlock=(EditText)findViewById(R.id.NameBlock);
        progressDialog=new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pictures/");
        alertDialog=new AlertDialog.Builder(this);
        firebaseAuth = FirebaseAuth.getInstance();
        addItemsOnSpinner2();
        addListenerOnSpinnerItemSelection();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = new Date();
            CurrentDate=simpleDateFormat.parse(simpleDateFormat.format(date));
            DeadlineDate=simpleDateFormat.parse("06-09-2019");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Vcode=(EditText)findViewById(R.id.Vcode);
        if(CurrentDate.compareTo(DeadlineDate)<=0){
            Vcode.setText("Not Recquired");
            Vcode.setEnabled(false);
        }



    }

    public void UploadProfilePicture(View view)
    {
        showFileChooser();

    }

    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"),CODE);
    }

    //handling the image chooser activity result
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();


            //Toast.makeText(RegisterActivity.this,filePath.toString(),Toast.LENGTH_LONG).show();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                UploadView.setImageBitmap(bitmap);



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
        edit.putString("profileImage"+Uid,encodedImage);
        edit.putString("name"+Uid,FullName);
        edit.putString("post"+Uid,post);
        edit.apply();
        AddFile();
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

        try {
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onSuccess(Uri uri) {


                                    url = uri.toString();
                                    ProfileUrl = url;
                                    DepartmentSpinner.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();

                                    alert.setMessage("Profile picture uploaded!");
                                    alert.show();

                                    SaveDataToFireBase();

                                    // downloadFile(AddAnnouncements.this,filePath.getLastPathSegment(),".pdf",DOWNLOAD_SERVICE,url);
                                }
                            });
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
        catch (Exception e)
        {
            AlertDialog.Builder alertDialog1=new AlertDialog.Builder(RegisterActivity.this);
            progressDialog.setMessage("Verifying please wait!");
            alertDialog1.setMessage("please upload a smaller size image!");
            alertDialog1.setPositiveButton("Ok",null);
            alertDialog1.show();
        }



    }

    public void VerifyUser()
    {

        progressDialog.show();

        final String vcode=Vcode.getText().toString();
        if(vcode.equals("Not Recquired")){
            flag = true;
            progressDialog.dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Register_user();
            }
        }else {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("New Member Verification Code");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.getKey().equals(vcode)) {

                            if (dataSnapshot1.getValue().equals("not used")) {
                                VcodeNo = dataSnapshot1.getKey();
                                flag = true;
                                progressDialog.dismiss();
                                Register_user();
                                break;
                            }


                        }
                    }
                    if (flag == false) {
                        alertDialog.setMessage("This Verification Code is not correct or It is already used!");
                        alertDialog.setPositiveButton("Ok", null);
                        alertDialog.show();
                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Register_user() {
        // File file = new File(filePath.toString());

                //Toast.makeText(RegisterActivity.this,String.valueOf(bitmap.getByteCount()),Toast.LENGTH_SHORT).show();

                firebaseAuth.createUserWithEmailAndPassword(Email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("New Member Verification Code");
                            databaseReference.child(VcodeNo).setValue("used");
                            current_user = FirebaseAuth.getInstance().getCurrentUser();
                            Uid = current_user.getUid();


                            CompressImage(bitmap);


                        } else {
                            alertDialog.setMessage("please enter a correct email Id");
                            alertDialog.setPositiveButton("Ok",null);
                            alertDialog.show();
                        }


                    }
                });
            }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveDataToFireBase()
    {
        // CompressImage(bitmap);


        post=Post.getText().toString();
        progressDialog.setTitle("Registeration");
        progressDialog.setMessage("Registering you! please wait");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String incharge = null;
        if (DepartmentName.equals("Technical")) {
            incharge = "Nitish Kumar";
            // Roles="As Technical Team Memeber , You should interact with your teammates and disucss about the projects. You should plan which member will make which module";
        } else if (DepartmentName.equals("Event Management")) {
            incharge = "Narendra Sinha";
        } else if (DepartmentName.equals("HR Department")) {
            incharge = "Rohan Pareek";
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Please select your department");
            alert.show();
            DepartmentSpinner.setDefaultFocusHighlightEnabled(true);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        User user = new User(FullName, "0", "Not yet evaluated!", DepartmentName, "false", DateofJoining, incharge, ProfileUrl,"0","0",post,"No","NA");
        databaseReference.child("users").child(Uid).setValue(user);
        alertDialog.setMessage("Registeration Succesfull!");
        alertDialog.setPositiveButton("Ok",null);
        alertDialog.show();
        progressDialog.dismiss();
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));


    }


 /*   private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imgPicker);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
*/



    public String CheckField(EditText editText)
    {
        Validation v=new Validation();
        status=v.CheckEmpty(editText);
        if(status.equals("OK")) {
            status = v.CheckIfShort(editText);
        }
       return status;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CheckVaildation(View view)
    {
        FullName=NameBlock.getText().toString();
         DateofJoining=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        DepartmentName=String.valueOf(DepartmentSpinner.getSelectedItem());
        Email=EmailIdBlock.getText().toString().trim();
        password=passwordBlock.getText().toString().trim();
        if(!CheckField(NameBlock).equals("OK"))
        {
            NameBlock.requestFocus();
            if(status.equals("Empty"))
            {
                NameBlock.setError("Please enter your name");
            }
            else if(status.equals("Short"))
            {
                NameBlock.setError("Name is too short");
            }
        }
        else if(!CheckField(EmailIdBlock).equals("OK"))
        {
            DOJ.requestFocus();

            if(status.equals("Empty"))
            {
                EmailIdBlock.setError("Please enter your Email ID");
            }
            else if(status.equals("Short"))
            {
                EmailIdBlock.setError("Email ID is too short");
            }
        }
        else if(!CheckField(passwordBlock).equals("OK"))
        {
            DOJ.requestFocus();

            if(status.equals("Empty"))
            {
                passwordBlock.setError("Please enter your password");
            }
            else if(status.equals("Short"))
            {
                passwordBlock.setError("password is too short ! please enter at least 6 characters");
            }
        }
        else if(filePath==null)
        {
            UploadView.requestFocus();
            alertDialog.setMessage("please select a profile picture");
            alertDialog.show();
        }
        else
            VerifyUser();
    }

    public void addListenerOnSpinnerItemSelection() {
        DepartmentSpinner = (Spinner) findViewById(R.id.spinner);
        DepartmentSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner2() {

        DepartmentSpinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("Select Your Department");
        list.add("Technical");
        list.add("Event Management");
        list.add("HR Department");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DepartmentSpinner.setSelected(true);
        DepartmentSpinner.setAdapter(dataAdapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
