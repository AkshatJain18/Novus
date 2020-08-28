package com.org.novus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class NewMemberVerificationCode extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText MobNoEditText;
    AlertDialog.Builder alertDialog;
    private static final int PERMISSION_SEND_SMS = 123;
    private static final int PERMISSION_READ_PHONE_STATE = 124;
    private static final int PERMISSION_READ_CONTACTS = 124;
    String MobNo;
    int Vcode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member_verification_code);
        progressDialog=new ProgressDialog(this);
        alertDialog=new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("Ok",null);

        MobNoEditText=(EditText)findViewById(R.id.MobNoEditText);

        if (ContextCompat.checkSelfPermission(NewMemberVerificationCode.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(NewMemberVerificationCode.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        }
    }
    public final int PICK_CONTACT = 2015;
    public void PickContact(View view)
    {        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);


            MobNo=cursor.getString(column);
            Toast.makeText(getApplicationContext(),MobNo,Toast.LENGTH_SHORT).show();
            MobNoEditText.setText(MobNo);
        }
    }
    public void Generate(View view)
    {
        if(MobNoEditText.getText().equals(""))
        {
            alertDialog.setMessage("please enter mobile no!");
            alertDialog.show();
        }
        else {
            progressDialog.setMessage("Generating Verification code!");
            progressDialog.show();
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            Random random = new Random();
            Vcode = random.nextInt(1000);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("New Member Verification Code").child(Integer.toString(Vcode)).setValue("not used");
            alertDialog.setMessage("New Member Verification code : " + Vcode);
            alertDialog.setPositiveButton("Ok", null);
            alertDialog.show();
            progressDialog.dismiss();
        }

    }
    public void CheckPermissions(View view)
    {
        if (ContextCompat.checkSelfPermission(NewMemberVerificationCode.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(NewMemberVerificationCode.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        }
         else if (ContextCompat.checkSelfPermission(NewMemberVerificationCode.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(NewMemberVerificationCode.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_READ_PHONE_STATE);
        }
        else if (ContextCompat.checkSelfPermission(NewMemberVerificationCode.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(NewMemberVerificationCode.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_READ_CONTACTS);
        }
        else {
            send();
        }
    }
    public void send()
    {

         MobNo=MobNoEditText.getText().toString();
        if(MobNo.equals("") )
        {
            alertDialog.setMessage("Please enter the mob no!");
            alertDialog.show();
        }
        else if(Vcode==0)
        {
            alertDialog.setMessage("Please first generate the verification code!");
            alertDialog.show();
        }
        else if(MobNo.length()<10)
        {
            alertDialog.setMessage("Please enter a ten digit mobile no!");
            alertDialog.show();

        }
        else
        {

                progressDialog.setMessage("Sending!");
                progressDialog.show();
                sendSMS(MobNo, "Welcome to Novus! Your Verification code for App registration is :" + Vcode +" . Kindly enter this at the time of registration"  );
        }
           // progressDialog.setMessage("Sending!");
            //progressDialog.show();
           // sendSMS(MobNo, "Welcome to Novus! Your Verification code for App registration is :" + Vcode +" . Kindly enter this at the time of registration"  );

        }



    public void sendSMS(String phoneNo, String msg) {
        SmsManager sms = SmsManager.getDefault();
        PendingIntent sentPI;
        String SENT = "SMS_SENT";

        sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);

        sms.sendTextMessage(phoneNo, null, msg, sentPI, null);
        alertDialog.setMessage("SMS sent!");
        alertDialog.show();

        progressDialog.dismiss();
    }
}
