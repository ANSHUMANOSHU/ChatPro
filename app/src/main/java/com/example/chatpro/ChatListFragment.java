package com.example.chatpro;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chatpro.adapters.AdapterChatList;
import com.example.chatpro.model.ChatModel;
import com.example.chatpro.model.UnreadMessages;
import com.example.chatpro.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<String> list = new ArrayList<>();
    private AdapterChatList adapterChatList;
    private ProgressBar progressBar;

    public ChatListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterChatList = new AdapterChatList(getActivity());
        recyclerView.setAdapter(adapterChatList);
        progressBar=view.findViewById(R.id.progressbar);
        fetchAllUsers();
        return view;
    }

    private void fetchAllUsers() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("interactionlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    list.add(ds.getValue(String.class));
                }
                final ArrayList<UserModel> userModels  = new ArrayList<>();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userModels.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            UserModel model = snapshot.getValue(UserModel.class);
                            if(FirebaseAuth.getInstance().getCurrentUser()!=null &&
                                    !model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && has(model.getUid()))
                                userModels.add(model);
                        }
                        showAllInteractedUsers(userModels);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAllInteractedUsers(ArrayList<UserModel> userModels) {
        final ArrayList<UnreadMessages> unreadMessagesArrayList = new ArrayList<>();
        for(UserModel model : userModels) {
            final UnreadMessages unreadMessages = new UnreadMessages();
            unreadMessages.userModel = model;
            unreadMessagesArrayList.add(unreadMessages);
            adapterChatList.setList(unreadMessagesArrayList);
            adapterChatList.notifyDataSetChanged();
        }
    }

    private boolean has(String uid) {
        for(String uid1 : list){
            if(uid1!=null && uid1.equals(uid))
                return true;
            }
        return false;
    }

}
