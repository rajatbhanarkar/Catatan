package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    Button Verify, SendOTP;
    EditText OTP, PhoneNumber;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    String codeSent;

    ArrayList<String> TeacherContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Verify = (Button)findViewById(R.id.btnverify);
        SendOTP = (Button)findViewById(R.id.btnsendotp);
        OTP = (EditText)findViewById(R.id.etotp);
        PhoneNumber = (EditText)findViewById(R.id.etphno);

        mAuth = FirebaseAuth.getInstance();
        TeacherContacts.add("9422823762"); //MAP
        TeacherContacts.add("9096896565"); //CN
        TeacherContacts.add("9921589563"); //SE
        TeacherContacts.add("9561560180"); //DP

        SendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Sending OTP...", Toast.LENGTH_SHORT).show();
                sendVerificationCode();
            }
        });

        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Verifying OTP...");
                progressDialog.show();
                verifySignIn();*/

                Intent intent = new Intent(LoginActivity.this, ProfileSetupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendVerificationCode(){

        String phno = "+91"+PhoneNumber.getText().toString();

        if(phno.length()!=13){
            Toast.makeText(this, "Please Enter a valid phone number!", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                OTP.setText(code);
                if(!OTP.getText().toString().equals("")){
                    Verify.performClick();
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this, "Sorry, Try again after sometime!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                codeSent = s;
            }

        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phno, 90, TimeUnit.SECONDS, this, mCallbacks );

    }

    private void verifySignIn(){

        String code = OTP.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        String Phno = PhoneNumber.getText().toString();

                        if (TeacherContacts.contains(Phno)){
                            Intent intent = new Intent(LoginActivity.this, TeacherWelcomeActivity.class);
                            if (Phno.equals(TeacherContacts.get(0))){
                                intent.putExtra("Subject","MAP");
                                intent.putExtra("Teacher","Prof. Kanak K Kalyani");
                            }
                            else if (Phno.equals(TeacherContacts.get(1))){
                                intent.putExtra("Subject","CN");
                                intent.putExtra("Teacher","Prof. S.S.Aote");
                            }
                            else if (Phno.equals(TeacherContacts.get(2))){
                                intent.putExtra("Subject","SE");
                                intent.putExtra("Teacher","Prof. Swati Heera");
                            }
                            else if (Phno.equals(TeacherContacts.get(3))){
                                intent.putExtra("Subject","DP");
                                intent.putExtra("Teacher","Prof. Rupali Vairaghade");
                            }
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Intent intent = new Intent(LoginActivity.this, ProfileSetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(LoginActivity.this, "Error in Login!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    }
}
