package com.example.chatpro.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatpro.ChatActivity;
import com.example.chatpro.ImageReceiverActivity;
import com.example.chatpro.MainActivity;
import com.example.chatpro.R;
import com.example.chatpro.Temp;
import com.example.chatpro.model.ChatModel;
import com.example.chatpro.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterUserList extends RecyclerView.Adapter<AdapterUserList.ViewHolder> {

    private ArrayList<UserModel> userModels = new ArrayList<>();
    private Context context;
    private boolean b = false;
    private Uri uri;
    public AdapterUserList(Context context) {
        this.context = context;
    }

    public void setType(boolean b,Uri uri){
        this.b = b;
        this.uri = uri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.username.setText(userModels.get(position).getName());
        holder.email.setText(userModels.get(position).getEmail());
        if (!"".equals(userModels.get(position).getProfileimage()))
            Glide.with(context).load(userModels.get(position).getProfileimage()).into(holder.userProfileImage);
        else
            holder.userProfileImage.setImageResource(R.drawable.person_high);
        if (!b) {
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    Temp.userModel = userModels.get(position);
                    context.startActivity(intent);
                }
            });
        }else {
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("Sending Image...");
                    dialog.setTitle("Connecting...");
                    dialog.setCancelable(false);
                    dialog.show();
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReference().child("attachment/" +
                                FirebaseAuth.getInstance().getCurrentUser().getUid() + "" + userModels.get(position).getUid()
                                + "" + new Date().getTime());
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
                                model.setMessage("");
                                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                                    model.setSenderuid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                model.setReceiveruid(userModels.get(position).getUid());
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
                                                    Intent intent = new Intent(context, ChatActivity.class);
                                                    Temp.userModel = userModels.get(position);
                                                    context.startActivity(intent);
                                                    ((ImageReceiverActivity)context).finish();
                                                    //addToUserInteractionList();
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
            });
        }
        if ("Online".equals(userModels.get(position).getOnlinestatus()))
            holder.status.setImageResource(R.color.green);
        else
            holder.status.setImageResource(R.color.red);
        holder.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.image_layout, null, false);
                TextView textView = view.findViewById(R.id.bigimagename);
                ImageView close, image;
                close = view.findViewById(R.id.close);
                image = view.findViewById(R.id.bigimage);

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                if (userModels.get(position).getProfileimage().isEmpty())
                    image.setImageResource(R.drawable.person_high);
                else
                    Glide.with(context).load(userModels.get(position).getProfileimage()).into(image);
                textView.setText(userModels.get(position).getName());
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public void setList(ArrayList<UserModel> userModels) {
        this.userModels = userModels;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage;
        TextView username, email;
        LinearLayout layout;
        ImageView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            email = itemView.findViewById(R.id.email);
            username = itemView.findViewById(R.id.username);
            layout = itemView.findViewById(R.id.itemuser_layout);
            status = itemView.findViewById(R.id.onlinestatus);
        }
    }
}

