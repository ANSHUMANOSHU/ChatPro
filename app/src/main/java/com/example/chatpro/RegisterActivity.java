package com.example.chatpro;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatpro.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText email,password,fullname;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.s_i_g_n_u_p);
        }
        //------------------------------------------------------------------------------------------
        fullname=findViewById(R.id.fullnamereg);
        email=findViewById(R.id.emailreg);
        password=findViewById(R.id.passwordreg);
        date=findViewById(R.id.date);
        //------------------------------------------------------------------------------------------
    }


    //---------------------------Registering User---------------------------------------------------

    public void register(View view) {

        if (fullname.getText().toString().isEmpty()) {
            fullname.setError("Empty Name");
            fullname.setFocusable(true);
            return;
        }

        if (email.getText().toString().isEmpty()) {
            email.setError("Empty Email");
            email.setFocusable(true);
            return;
        }
        if(password.getText().toString().isEmpty()){
            password.setError("Empty Password");
            password.setFocusable(true);
            return;
        }
        if (password.getText().toString().length() <= 6) {
            password.setError("Length must be > 6");
            password.setFocusable(true);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError("Invalid  Email");
            email.setFocusable(true);
            return;
        }
        //---------------------------progress dialog-----------------------------------------------

        final ProgressDialog dialog =new ProgressDialog(this);
        dialog.setTitle("Connecting...");
        dialog.setMessage("Registering User");
        dialog.setCancelable(false);
        dialog.show();

        //------------------------------------------------------------------------------------------

        //-----------------------------------------saving credentials-------------------------------

        FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Registered Successfully...", Toast.LENGTH_SHORT).show();
                            String UId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String name = fullname.getText().toString();
                            String Email = email.getText().toString();
                            String Date = date.getText().toString();
                            String profileImage = "";
                            String coverImage = "";
                            String phone = "XXX-XXX-XXX-X";
                            String status = "Hey There I am using ChatPro...";

                            UserModel userModel =new UserModel();
                            userModel.setUid(UId);
                            userModel.setName(name);
                            userModel.setEmail(Email);
                            userModel.setDate(Date);
                            userModel.setProfileimage(profileImage);
                            userModel.setCoverimage(coverImage);
                            userModel.setPhone(phone);
                            userModel.setStatus(status);
                            userModel.setOnlinestatus("Online");

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                            reference.child(UId).setValue(userModel);


                            Intent intent =new Intent(RegisterActivity.this,DashBoard.class);
                            startActivity(intent);
                            finish();
                        }
                        dialog.dismiss();
                    }
                })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Registration Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------DatePicker----------------------------------------------

    public void dateChoose(View view) {

        DatePickerDialog datePickerDialog=new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        },2000,1,1);

        datePickerDialog.show();

    }
    //----------------------------------------------------------------------------------------------
}
