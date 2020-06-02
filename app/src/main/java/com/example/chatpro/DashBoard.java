package com.example.chatpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.util.Date;

public class DashBoard extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        bottomNavigationView = findViewById(R.id.navigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        getSupportActionBar().setTitle("Chats");
        ChatListFragment chatListFragment = new ChatListFragment();
        fragmentTransaction.replace(R.id.frame, chatListFragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout").setMessage("Are you sure you want to Logout ?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(DashBoard.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(DashBoard.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).setCancelable(false);
                builder.create().show();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStatus("Online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("Online");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setStatus("Online");
    }

    private String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private void setStatus(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").
                child(UID).child("onlinestatus");
        reference.setValue(status);

    }

    @Override
    public void finish() {
        super.finish();
        setStatus(new Date().getTime() + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStatus(new Date().getTime() + "");
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        setStatus(new Date().getTime() + "");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.chat:
                getSupportActionBar().setTitle("Chats");
                ChatListFragment chatListFragment = new ChatListFragment();
                fragmentTransaction.replace(R.id.frame, chatListFragment);
                fragmentTransaction.commit();
                break;
            case R.id.users:
                getSupportActionBar().setTitle("Users");
                OnlineUserFragment userFragment = new OnlineUserFragment();
                fragmentTransaction.replace(R.id.frame, userFragment);
                fragmentTransaction.commit();
                break;
            case R.id.profile:
                if (FirebaseAuth.getInstance().getCurrentUser().getUid() == null)
                    break;
                getSupportActionBar().setTitle("Profile");
                ProfileFragment profileFragment = new ProfileFragment(DashBoard.this, true, FirebaseAuth.getInstance().getCurrentUser().getUid());
                fragmentTransaction.replace(R.id.frame, profileFragment);
                fragmentTransaction.commit();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Profile Image...");
            dialog.setTitle("Connecting...");
            dialog.setCancelable(false);
            dialog.show();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("user/profile_" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> taskUri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!taskUri.isSuccessful()) {
                        }
                        String url = taskUri.getResult().toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).child("profileimage");
                        ref.setValue(url);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog.dismiss();
                    }
                });
            }


        }
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error While Cropping Image", Toast.LENGTH_SHORT).show();
        }
    }
}
