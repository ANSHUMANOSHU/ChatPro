package com.example.chatpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class OtherUserDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_details);
        String uid = Temp.UID;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        getSupportActionBar().setTitle("Profile");
        ProfileFragment profileFragment = new ProfileFragment(this,false,uid);
        fragmentTransaction.replace(R.id.frameothers,profileFragment);
        fragmentTransaction.commit();

    }
}
