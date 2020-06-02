package com.example.chatpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSavedUser();
    }


    private void checkSavedUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(MainActivity.this,DashBoard.class);
            startActivity(intent);
            finish();
        }
    }

    public void registerClick(View view) {
        startActivity(new Intent(MainActivity.this,RegisterActivity.class));
        finish();
    }

    public void loginClick(View view) {
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }

    public void showAlertDilaog(String url,String name){
        View view = LayoutInflater.from(this).inflate(R.layout.image_layout,null,false);
        TextView textView = view.findViewById(R.id.bigimagename);
        ImageView close , image;
        close = view.findViewById(R.id.close);
        image=view.findViewById(R.id.bigimage);

        final Dialog dialog =new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        if(url.isEmpty())
            image.setImageResource(R.drawable.person_high);
        else
            Glide.with(this).load(url).into(image);
        textView.setText(name);
        dialog.show();
    }


    // TODO------------------show notifications
    //send video
    //send docs
    //group chat
    //unread msg count
}
