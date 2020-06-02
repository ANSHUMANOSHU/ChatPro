package com.example.chatpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chatpro.adapters.AdapterUserList;
import com.example.chatpro.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageReceiverActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_receiver);
        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setHasFixedSize(true);
        adapterUserList  =new AdapterUserList(this);
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        adapterUserList.setType(true,uri);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterUserList);
        adapterUserList.notifyDataSetChanged();
        progressBar= findViewById(R.id.progressbar);
        fetchAllUsers();
    }

    private RecyclerView recyclerView;
    private AdapterUserList adapterUserList;
    private ArrayList<UserModel> userModels =  new ArrayList<>();
    private ProgressBar progressBar;

    private void fetchAllUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModels.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null &&
                            !model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        userModels.add(model);
                }
                adapterUserList.setList(userModels);
                adapterUserList.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
