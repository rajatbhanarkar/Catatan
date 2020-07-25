package com.example.catatan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TeacherWelcomeActivity extends AppCompatActivity {

    TextView TeacherName;
    Button ViewContents, UploadFiles;
    String Subject, Teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_welcome);

        Subject = getIntent().getStringExtra("Subject");
        Teacher = getIntent().getStringExtra("Teacher");

        TeacherName = (TextView)findViewById(R.id.tvteachername);
        ViewContents = (Button)findViewById(R.id.btnviewcontents);
        UploadFiles = (Button)findViewById(R.id.btnuploadfiles);

        TeacherName.setText("Welcome "+Teacher+"!");

        ViewContents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherWelcomeActivity.this, CourseContentsActivity.class);
                intent.putExtra("Subject",Subject);
                startActivity(intent);
            }
        });

        UploadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherWelcomeActivity.this, UploadFilesActivity.class);
                intent.putExtra("Subject",Subject);
                startActivity(intent);
            }
        });
    }
}
