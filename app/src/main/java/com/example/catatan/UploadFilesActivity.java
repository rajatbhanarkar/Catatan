package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadFilesActivity extends AppCompatActivity {

    Spinner UnitSpinner;
    EditText TopicName;
    TextView SubjectName;
    ImageView SubjectLogo;
    Button ChooseFile, UploadFile;
    String[] Units = {"Unit 1", "Unit 2", "Unit 3", "Unit 4", "Unit 5", "Unit 6"};
    DatabaseReference myRef;
    StorageReference storageReference;
    String Subject;
    Uri FilePath;

    public void initData(String Subject){

        if (Subject.equals("CN")){
            SubjectName.setText("Computer Networks");
            SubjectLogo.setImageResource(R.drawable.cn);
        }
        else if(Subject.equals("SE")){
            SubjectName.setText("Software Engineering");
            SubjectLogo.setImageResource(R.drawable.se);
        }
        else if(Subject.equals("DP")){
            SubjectName.setText("Design Patterns");
            SubjectLogo.setImageResource(R.drawable.dp);
        }
        else if(Subject.equals("MAP")){
            SubjectName.setText("Mobile Application Programming");
            SubjectLogo.setImageResource(R.drawable.map);
        }
        else if(Subject.equals("DAA")){
            SubjectName.setText("Design & Analysis of Algorithms");
            SubjectLogo.setImageResource(R.drawable.daa);
        }
        else{
            SubjectName.setText("Exam Papers");
            SubjectLogo.setImageResource(R.drawable.test);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);

        storageReference = FirebaseStorage.getInstance().getReference();
        Subject = getIntent().getStringExtra("Subject");
        UnitSpinner = (Spinner)findViewById(R.id.unitspinner);
        TopicName = (EditText)findViewById(R.id.ettopicname);
        ChooseFile = (Button)findViewById(R.id.btnselectfile);
        UploadFile = (Button)findViewById(R.id.btnuploadfile);
        SubjectName = (TextView)findViewById(R.id.tvsubjectname);
        SubjectLogo = (ImageView)findViewById(R.id.ivsubjectlogo);

        initData(Subject);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Units);
        UnitSpinner.setAdapter(arrayAdapter);

        ChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 3000);
            }
        });

        UploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef = FirebaseDatabase.getInstance().getReference().child("File Links").child("CSE").child("Semester 5").child(Subject).child(UnitSpinner.getSelectedItem().toString());

                final String fname = ""+Subject+" "+UnitSpinner.getSelectedItem().toString()+" "+TopicName.getText().toString()+".pdf";
                final StorageReference fileRef = storageReference.child("CSE/Semester 5/"+Subject+"/"+fname);

                if (FilePath != null){
                    final ProgressDialog progressDialog = new ProgressDialog(UploadFilesActivity.this);
                    progressDialog.setTitle("Uploading file...");
                    progressDialog.show();

                    fileRef.putFile(FilePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FileDetails fileDetails = new FileDetails();
                                            fileDetails.setFileName(fname);
                                            fileDetails.setFileUrl(uri.toString());
                                            myRef.push().setValue(fileDetails);
                                        }
                                    });
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadFilesActivity.this, "File Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UploadFilesActivity.this, "Error! File Uploading Unsuccessful!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    long progress = 100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                                    progressDialog.setMessage(""+progress+"% Uploaded");
                                }
                            });
                }
                else{
                    Toast.makeText(UploadFilesActivity.this, "File path not received!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3000) {
            if (resultCode == RESULT_OK) {
                FilePath = Uri.parse(data.getDataString());
                Toast.makeText(this, ""+FilePath, Toast.LENGTH_LONG).show();
                Toast.makeText(this, ""+FilePath, Toast.LENGTH_LONG).show();
                Toast.makeText(this, ""+FilePath, Toast.LENGTH_LONG).show();
                Toast.makeText(this, ""+FilePath, Toast.LENGTH_LONG).show();
                Toast.makeText(this, ""+FilePath, Toast.LENGTH_LONG).show();
                Toast.makeText(this, ""+FilePath, Toast.LENGTH_LONG).show();
            }
        }
    }
}
