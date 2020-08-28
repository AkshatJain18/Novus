package com.org.novus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.provider.ContactsContract;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText Email, Password;
    String email,password;
    FirebaseAuth firebaseAuth;
    FirebaseUser current_user;
    private ProgressDialog progressDialog;
    AlertDialog.Builder alert;


    public void Register(View view) {
        Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    }


    public void ChangePassword(View view)
    {
        Validation v=new Validation();
        if (v.CheckEmpty(Email).equals("Empty")) {
            Email.requestFocus();
            Email.setError("Email is Empty!");
        }
        else {
            progressDialog.setMessage("Please wait");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            email = Email.getText().toString().trim();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                alert.setMessage("We have sent password reset email to your mail ID! Please open the same to reset the password");
                                alert.show();
                            } else {
                                progressDialog.dismiss();
                                alert.setMessage("Could not send reset email! Please enter correct email ID");
                                alert.show();
                            }
                        }
                    });
        }
    }
    public void doLogin()
    {
         email = Email.getText().toString().trim();

         password = Password.getText().toString().trim();
        Validation v=new Validation();
        if (v.CheckEmpty(Email).equals("Empty")) {
            Email.requestFocus();
            Email.setError("Email is Empty!");
        } else if (v.CheckIfShort(Email).equals("Short")) {
            Email.requestFocus();
            Email.setError("Email is too short");
        } else if (v.CheckEmpty(Password).equals("Empty")) {
            Password.requestFocus();
            Password.setError("password is Empty!");
        } else if (v.CheckIfShort(Password).equals("Short")) {
            Password.requestFocus();
            Password.setError("password is too short ! please enter at least 6 character password");
        } else {
            progressDialog.setMessage("Signing you in Please Wait...");

            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                current_user=firebaseAuth.getCurrentUser();
                                //and open profile activity
                                if(current_user.isEmailVerified()) {
                                    // Toast.makeText(RegisterActivity.this, "Registeration succesful!", Toast.LENGTH_LONG).show();
                                    final ArrayList arrayList=new ArrayList();
                                    final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Removed Users");
                                  databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                          for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                          {
                                              String check=dataSnapshot1.child("Uid").getValue().toString();
                                              arrayList.add(check);
                                          }
                                          if(arrayList.contains(current_user.getUid()))
                                          {

                                                                         current_user.delete()
                                                                          .addOnCompleteListener (new OnCompleteListener<Void>() {
                                                                              @Override
                                                                              public void onComplete(@NonNull Task<Void> task) {
                                                                                  if (task.isSuccessful()) {
                                                                                      alert.setMessage("You are Fired from the organsiation!");
                                                                                      alert.show();
                                                                                  } else {
                                                                                      Log.e("TAG", "User account deletion unsucessful.");
                                                                                  }
                                                                              }
                                                                          });


                                                              // databaseReference.child(firebaseAuth.getUid()).removeValue();
                                          }
                                          else
                                          {
                                              Intent intent=new Intent(getApplicationContext(),LandingPage.class);
                                              startActivity(intent);
                                          }

                                      }

                                      @Override
                                      public void onCancelled(@NonNull DatabaseError databaseError) {

                                      }
                                  });

                                }
                                else
                                {
                                    Verify();
                                }

                            } else {
                                progressDialog.dismiss();
                                alert.setMessage("Invalid Email or Password!");
                                alert.show();
                                //Toast.makeText(LoginActivity.this, "COuld not sign in !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    public void login(View view) {


        Email = (EditText) findViewById(R.id.EmailIdBlock);

        Password = (EditText) findViewById(R.id.PasswordBlock);

        doLogin();

    }
    public void Verify()
    {
        current_user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    alert.setMessage("Please verify your Email to complete Signup Process, Verification Email sent to " + current_user.getEmail());
                    firebaseAuth.signOut();
                    alert.show();
                }
                else
                {
                    alert.setMessage("Could not Send Verification Email! Please Enter correct Email ID");
                    alert.show();
                    firebaseAuth.signOut();
                    Email.requestFocus();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         alert=new AlertDialog.Builder(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //firebaseAuth.signOut();
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        Email = (EditText) findViewById(R.id.EmailIdBlock);

        Password = (EditText) findViewById(R.id.PasswordBlock);

        //if getCurrentUser does not returns null
        if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), LandingPage.class));
        }

        progressDialog=new ProgressDialog(LoginActivity.this);
        alert.setPositiveButton("Ok",null);
    }
}
