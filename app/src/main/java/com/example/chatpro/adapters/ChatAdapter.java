package com.example.chatpro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.chatpro.R;
import com.example.chatpro.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.example.chatpro.Util.Utilities;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private Context context;
    private ArrayList<ChatModel> chats;

    public ChatAdapter(Context context, ArrayList<ChatModel> chats) {
        this.context = context;
        this.chats = chats;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       if(viewType == LEFT){
           View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
           MyViewHolder myViewHolder = new MyViewHolder(view);
           return myViewHolder;
       }
       else{
           View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
           MyViewHolder myViewHolder = new MyViewHolder(view);
           return myViewHolder;
       }
    }


    @Override
    public int getItemViewType(int position) {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null && chats.get(position).senderuid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return RIGHT;
        }
        else{
            return LEFT;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatModel chatModel = chats.get(position);
        holder.time.setText(Utilities.getTime(Long.parseLong(chatModel.getStamp())));
        holder.status.setText(chatModel.isIsseen() ? "Seen" : "Delivered");
        holder.message.setText(chatModel.getMessage());
        if("image".equals(chatModel.getType())){
            Glide.with(context).load(chatModel.imageurl).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        }
        else{
            holder.image.setVisibility(View.GONE);
        }
        if(chatModel.getMessage().isEmpty())
            holder.message.setVisibility(View.GONE);
        else {
            holder.message.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setList(ArrayList<ChatModel> arrayList) {
        this.chats=arrayList;
    }

    //View Holder Class
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView message,time,status;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            image =itemView.findViewById(R.id.image);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
        }
    }
}
