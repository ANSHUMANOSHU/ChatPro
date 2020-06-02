package com.example.chatpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatpro.Util.Utilities;
import com.example.chatpro.adapters.ChatAdapter;
import com.example.chatpro.model.ChatModel;
import com.example.chatpro.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 1;
    private ChatAdapter adapter;
    private ArrayList<ChatModel> arrayList = new ArrayList<>();
    private DatabaseReference reference;
    private UserModel recUid;
    private ImageView profileImage, sendBtn;
    private TextView receiverName, status;
    private EditText message;
    private RecyclerView recyclerView;
    private DatabaseReference reference1;
    private DatabaseReference reference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        getSupportActionBar().hide();
        profileImage = findViewById(R.id.image_profile);
        sendBtn = findViewById(R.id.send);
        receiverName = findViewById(R.id.receiverName);
        status = findViewById(R.id.status);
        message = findViewById(R.id.message);
        recyclerView = findViewById(R.id.recyclerviewChat);
        setStatus("Online");

        recUid = Temp.userModel;
        setUserDetails();
        attachListener();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString().trim();
                if (!msg.isEmpty()) {
                    reference = FirebaseDatabase.getInstance().getReference("chats");
                    String nodename = new Date().getTime() + FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ChatModel model = new ChatModel();
                    model.setMessage(msg);
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        model.setSenderuid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    model.setReceiveruid(recUid.getUid());
                    model.setStamp(new Date().getTime() + "");
                    model.setIsseen(false);
                    model.setType("text");
                    model.setImageurl("");
                    model.setKey(nodename);
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        reference.child(nodename).setValue(model)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        message.setText("");
                                        addToUserInteractionList();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                }
            }
        });
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ChatAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getAllMessages();
    }

    private void addToUserInteractionList() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference re = FirebaseDatabase.getInstance().getReference("interactionlist")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(recUid.getUid());
            re.setValue(recUid.getUid());

            DatabaseReference re1 = FirebaseDatabase.getInstance().getReference("interactionlist")
                    .child(recUid.getUid()).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            re1.setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
    }

    private void setStatus(String status) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("onlinestatus");
            reference.setValue(status);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reference1.removeEventListener(valueEventListener);
        reference2.removeEventListener(valueEventListener2);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            recUid = dataSnapshot.getValue(UserModel.class);
            setUserDetails();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            arrayList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                ChatModel model = snapshot.getValue(ChatModel.class);
                if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                        (model.getReceiveruid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                && model.getSenderuid().equals(recUid.getUid()))
                        || FirebaseAuth.getInstance().getCurrentUser() != null
                        && (model.getSenderuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        && model.getReceiveruid().equals(recUid.getUid())))
                    arrayList.add(model);
            }

            for (ChatModel c : arrayList) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && c.getReceiveruid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats")
                            .child(c.getKey()).child("isseen");
                    reference.setValue(true);
                }
            }

            if (arrayList.size() >= 1)
                recyclerView.scrollToPosition(arrayList.size() - 1);
            adapter.setList(arrayList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }

    };

    private void attachListener() {
        reference1 = FirebaseDatabase.getInstance().getReference("users").child(recUid.getUid());
        reference1.addValueEventListener(valueEventListener);
    }

    private void setUserDetails() {
        if (!"".equals(recUid.getProfileimage()))
            Glide.with(this).load(recUid.getProfileimage()).into(profileImage);
        else
            profileImage.setImageResource(R.drawable.person_high);
        receiverName.setText(recUid.getName());
        if ("Online".equals(recUid.getOnlinestatus()))
            status.setText("Online");
        else if (recUid.getOnlinestatus().isEmpty())
            status.setText("Not Available");
        else {
            long t = new Date().getTime();
            try {
                t = Long.parseLong(recUid.getOnlinestatus());
            } catch (Exception e) {
            }
            status.setText("Last seen : " + Utilities.getTime(t));
        }
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        setStatus(new Date().getTime() + "");
    }


    private void getAllMessages() {
        reference2 = FirebaseDatabase.getInstance().getReference("chats");
        reference2.addValueEventListener(valueEventListener2);
    }

    public void attachment(View view) {
        if (checkStoragePermission()) {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, IMAGE_REQUEST);
        } else {
            Toast.makeText(ChatActivity.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkStoragePermission() {
        //TODO------storage permission
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST) {
            uri = data.getData();
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Sending Image...");
            dialog.setTitle("Connecting...");
            dialog.setCancelable(false);
            dialog.show();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("attachment/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "" + recUid.getUid() + "" + new Date().getTime());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> taskUri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!taskUri.isSuccessful()) {
                        }
                        String url = taskUri.getResult().toString();

                        DatabaseReference referenc = FirebaseDatabase.getInstance().getReference("chats");
                        String nodename = new Date().getTime() + FirebaseAuth.getInstance().getCurrentUser().getUid();
                        ChatModel model = new ChatModel();
                        model.setMessage(message.getText().toString());
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            model.setSenderuid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        model.setReceiveruid(recUid.getUid());
                        model.setStamp(new Date().getTime() + "");
                        model.setIsseen(false);
                        model.setType("image");
                        model.setImageurl(url);
                        model.setKey(nodename);
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            referenc.child(nodename).setValue(model)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            message.setText("");
                                            addToUserInteractionList();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
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
    }

    public void showUserProfileDetails(View view) {
        Temp.UID = recUid.getUid();
        startActivity(new Intent(ChatActivity.this, OtherUserDetails.class));
    }
}
