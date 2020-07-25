package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileSetupActivity extends AppCompatActivity {

    EditText Name;
    AutoCompleteTextView Branch, Semester;
    ProgressDialog progressDialog;
    Button Save;
    DatabaseReference myRef;
    UserProfile userProfile;
    long maxid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        Name = (EditText)findViewById(R.id.etname);
        Branch = (AutoCompleteTextView)findViewById(R.id.actvbranch);
        Semester = (AutoCompleteTextView)findViewById(R.id.actvsem);
        Save = (Button)findViewById(R.id.btnsavedetails);

        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(ProfileSetupActivity.this, android.R.layout.simple_list_item_1, new String[]{"CSE","IT","EC","EN","EDT","CIVIL","IND","MECH","CHEM","EP","MBA"});
        ArrayAdapter<String> semAdapter = new ArrayAdapter<>(ProfileSetupActivity.this, android.R.layout.simple_list_item_1, new String[]{"Semester 1","Semester 2","Semester 3","Semester 4","Semester 5","Semester 6","Semester 7","Semester 8"});

        Branch.setAdapter(branchAdapter);
        Branch.setThreshold(1);
        Semester.setAdapter(semAdapter);
        Semester.setThreshold(1);

        myRef = FirebaseDatabase.getInstance().getReference().child("User Profile");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxid = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileSetupActivity.this, "There was an error!", Toast.LENGTH_SHORT).show();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(ProfileSetupActivity.this);
                progressDialog.setTitle("Saving Details...");
                progressDialog.setMessage("Sit back, and relax! \nWe're customizing the study material for you!");
                progressDialog.show();

                if(Name.getText().toString().equals("") || Branch.getText().toString().equals("") || Semester.getText().toString().equals("")){
                    Toast.makeText(ProfileSetupActivity.this, "All fields are necessary!", Toast.LENGTH_SHORT).show();
                }
                else{
                    userProfile = new UserProfile();

                    userProfile.setName(Name.getText().toString());
                    userProfile.setBranch(Branch.getText().toString());
                    userProfile.setSemester(Semester.getText().toString());
                    userProfile.setPhoneNo(getIntent().getStringExtra("PhoneNo"));

                    myRef.child(String.valueOf(maxid + 1)).setValue(userProfile);
                    Toast.makeText(ProfileSetupActivity.this, "Profile Registered Successfully!", Toast.LENGTH_SHORT).show();

                    CountDownTimer countDownTimer = new CountDownTimer(3000,1000) {
                        @Override
                        public void onTick(long l) { }

                        @Override
                        public void onFinish() {
                            progressDialog.dismiss();
                            Intent intent = new Intent(ProfileSetupActivity.this, HomePageActivity.class);
                            intent.putExtra("Branch",userProfile.getBranch());
                            intent.putExtra("Sem",userProfile.getSemester());
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                }
            }
        });

    }
}
