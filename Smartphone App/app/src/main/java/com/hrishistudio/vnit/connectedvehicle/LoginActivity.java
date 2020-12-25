package com.hrishistudio.vnit.connectedvehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passInput;
    private CoordinatorLayout frame;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = (EditText)findViewById(R.id.user_email);
        passInput = (EditText)findViewById(R.id.user_password);
        frame = (CoordinatorLayout)findViewById(R.id.login_coordinator);

        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("Please wait..");
        mDialog.setTitle("Signing in");
    }

    public void loginUser(View view){
        String email = emailInput.getText().toString().trim();
        String password = passInput.getText().toString().trim();

        if (email.isEmpty()){
            Snackbar.make(frame, "Please enter a valid email", Snackbar.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()){
            Snackbar.make(frame, "Please enter your password", Snackbar.LENGTH_SHORT).show();
        }
        else {
            mDialog.show();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.dismiss();
                    if (task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Snackbar.make(frame, "Error occurred." + task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void goToSignUp(View view){
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        finish();
    }
}