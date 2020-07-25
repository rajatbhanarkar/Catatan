package com.example.catatan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class HomePageActivity extends AppCompatActivity {

    ImageView CN, MAP, DAA, SE, DP, Exam;
    String Branch, Sem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        CN = (ImageView)findViewById(R.id.ivcn);
        MAP = (ImageView)findViewById(R.id.ivmap);
        DAA = (ImageView)findViewById(R.id.ivdaa);
        SE = (ImageView)findViewById(R.id.ivse);
        DP = (ImageView)findViewById(R.id.ivdp);
        Exam = (ImageView)findViewById(R.id.ivexam);

        Branch = getIntent().getStringExtra("Branch");
        Sem = getIntent().getStringExtra("Sem");

        if (Branch.equals("CSE") && Sem.equals("Semester 5")){
            CN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePageActivity.this, CourseContentsActivity.class);
                    intent.putExtra("Subject","CN");
                    startActivity(intent);
                }
            });

            MAP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePageActivity.this, CourseContentsActivity.class);
                    intent.putExtra("Subject","MAP");
                    startActivity(intent);
                }
            });

            DAA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePageActivity.this, CourseContentsActivity.class);
                    intent.putExtra("Subject","DAA");
                    startActivity(intent);
                }
            });

            SE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePageActivity.this, CourseContentsActivity.class);
                    intent.putExtra("Subject","SE");
                    startActivity(intent);
                }
            });

            DP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePageActivity.this, CourseContentsActivity.class);
                    intent.putExtra("Subject","DP");
                    startActivity(intent);
                }
            });

            Exam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomePageActivity.this, CourseContentsActivity.class);
                    intent.putExtra("Subject","Exam");
                    startActivity(intent);
                }
            });
        }
        else{
            CN.setImageResource(0);
            MAP.setImageResource(0);
            DAA.setImageResource(0);
            SE.setImageResource(0);
            DP.setImageResource(0);
            Exam.setImageResource(0);

            Toast.makeText(this, "Sorry, No data found!", Toast.LENGTH_LONG).show();
        }
    }
}
