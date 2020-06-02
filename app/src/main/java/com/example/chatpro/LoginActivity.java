package com.example.chatpro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar =getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.l_o_g_i_n);
        }
        //------------------------------------------------------------------------------------------
           email = findViewById(R.id.email);
           password = findViewById(R.id.password);
        //------------------------------------------------------------------------------------------

    }

    //----------------------------------Logging In User---------------------------------------------
    public void login(View view) {
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
        final ProgressDialog dialog =new ProgressDialog(this);
        dialog.setTitle("Connecting...");
        dialog.setMessage("Checking Credentials");
        dialog.setCancelable(false);
        dialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Logged IN Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(LoginActivity.this,DashBoard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
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
}
