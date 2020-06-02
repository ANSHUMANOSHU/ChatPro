package com.example.chatpro.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatpro.ChatActivity;
import com.example.chatpro.R;
import com.example.chatpro.Temp;
import com.example.chatpro.model.UnreadMessages;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyViewHolder> {


    private Context context;
    private ArrayList<UnreadMessages> unreadMessages = new ArrayList<>();

    public AdapterChatList(Context context) {
        this.context = context;
    }
    public void setList(ArrayList<UnreadMessages> unreadMessages){
        this.unreadMessages = unreadMessages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.user_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if(unreadMessages.get(position).userModel.getProfileimage().isEmpty()){
            holder.proFileImage.setImageResource(R.drawable.person_high);
        }
        else{
            Glide.with(context).load(unreadMessages.get(position).userModel.getProfileimage()).into(holder.proFileImage);
        }
        holder.username.setText(unreadMessages.get(position).userModel.getName());
        holder.email.setText(unreadMessages.get(position).userModel.getEmail());

        holder.itemchat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Temp.userModel = unreadMessages.get(position).userModel;
                context.startActivity(new Intent(context, ChatActivity.class));
            }
        });
        holder.proFileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.image_layout,null,false);
                TextView textView = view.findViewById(R.id.bigimagename);
                ImageView close , image;
                close = view.findViewById(R.id.close);
                image=view.findViewById(R.id.bigimage);

                final Dialog dialog =new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                if(unreadMessages.get(position).userModel.getProfileimage().isEmpty())
                    image.setImageResource(R.drawable.person_high);
                else
                    Glide.with(context).load(unreadMessages.get(position).userModel.getProfileimage()).into(image);
                textView.setText(unreadMessages.get(position).userModel.getName());
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return unreadMessages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView proFileImage;
        TextView username,email;
        RelativeLayout itemchat_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            proFileImage = itemView.findViewById(R.id.userProfileImage);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            itemchat_layout = itemView.findViewById(R.id.itemchat_layout);
        }
    }
}
