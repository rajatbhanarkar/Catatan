package com.example.catatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CourseContentsActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    ArrayList<String> Header;
    HashMap<String, ArrayList<String>> Items;
    HashMap<String, ArrayList<String>> MainLinks;
    FileDetails fileDetails;

    BroadcastReceiver onComplete;
    File MyPath;
    String Subject = "";

    TextView SubjectName;
    ImageView SubjectLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_contents);

        Subject = getIntent().getStringExtra("Subject");

        expandableListView = (ExpandableListView)findViewById(R.id.explv);
        SubjectName = (TextView)findViewById(R.id.tvsubjectname);
        SubjectLogo = (ImageView)findViewById(R.id.ivsubjectlogo);

        initData(Subject);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Contents");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        CountDownTimer countDownTimer = new CountDownTimer(3000, 3000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                progressDialog.dismiss();

                expandableListAdapter = new ExpandableListAdapter(Header, Items, MainLinks);
                expandableListView.setAdapter(expandableListAdapter);

                expandableListView.deferNotifyDataSetChanged();
            }
        }.start();
    }

    public void initData(String Subject){

        Header = new ArrayList<>(); Items = new HashMap<>(); MainLinks = new HashMap<>();
        Header.add("Unit 1"); Header.add("Unit 2"); Header.add("Unit 3"); Header.add("Unit 4"); Header.add("Unit 5"); Header.add("Unit 6");

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
        getData(Subject);
    }

    public void getData(String subject){
        for(int i=0 ; i<6 ; i++){
            final ArrayList<String> Unit = new ArrayList<>();
            final ArrayList<String> Links = new ArrayList<>();

            Query idQuery = FirebaseDatabase.getInstance().getReference("File Links").child("CSE").child("Semester 5").child(subject).child("Unit "+(i+1));
            idQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            fileDetails = snapshot.getValue(FileDetails.class);
                            Unit.add(fileDetails.getFileName());
                            Links.add(fileDetails.getFileUrl());
                        }
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
            Items.put(Header.get(i), Unit);
            MainLinks.put(""+i, Links);
        }
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter{

        ArrayList<String> Headers;
        HashMap<String, ArrayList<String>> Items;
        HashMap<String, ArrayList<String>> MainLinks;

        ExpandableListAdapter(ArrayList<String> headers, HashMap<String, ArrayList<String>> items, HashMap<String, ArrayList<String>> links) {
            Headers = headers; Items = items; MainLinks = links;
        }

        @Override
        public int getGroupCount() {
            return Headers.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return Items.get(Headers.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return Headers.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return Items.get(Headers.get(i)).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list_group, null);
            TextView header = (TextView)view.findViewById(R.id.tvexpheader);
            header.setText(""+getGroup(i));
            return view;
        }

        @Override
        public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list_item, null);
            final TextView item = (TextView)view.findViewById(R.id.tvexpitem);
            final ImageView FileStatus = (ImageView)view.findViewById(R.id.ivfilestatus);
            item.setText(""+getChild(i,i1));

            if(ContextCompat.checkSelfPermission(CourseContentsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(CourseContentsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(CourseContentsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 3000);
            }

            final File file = new File(getExternalFilesDir(null).getAbsolutePath()+"/Catatan/"+Items.get(Headers.get(i)).get(i1));

            if (file.exists()){
                FileStatus.setImageResource(R.drawable.ic_done);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
            else{
                FileStatus.setImageResource(R.drawable.ic_file_download);
                FileStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(CourseContentsActivity.this, "Downloading file...", Toast.LENGTH_SHORT).show();
                        onComplete = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                            FileStatus.setImageResource(R.drawable.ic_done);
                            item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri uri = Uri.fromFile(file);
                                intent.setDataAndType(uri, "application/pdf");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                }
                            });
                            }
                        };
                        startDownload(i, i1);
                    }
                });
            }
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

    public void startDownload(int i, int i1){
        Uri uri = Uri.parse(MainLinks.get(""+i).get(i1));
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(Items.get(Header.get(i)).get(i1));
        request.setDescription("Downloading file...");

        MyPath = new File("Catatan");
        if (!MyPath.exists()){ MyPath.mkdir(); }

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getApplicationContext(),MyPath.getAbsolutePath(),Items.get(Header.get(i)).get(i1));

        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 3000){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length>0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
