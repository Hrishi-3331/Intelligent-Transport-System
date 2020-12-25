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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passInput;
    private EditText cpassInput;
    private EditText nameInput;
    private CoordinatorLayout frame;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailInput = (EditText)findViewById(R.id.reg_user_email);
        nameInput = (EditText)findViewById(R.id.reg_user_name);
        passInput = (EditText)findViewById(R.id.reg_user_password);
        cpassInput = (EditText)findViewById(R.id.reg_user_conf_password);
        frame = (CoordinatorLayout) findViewById(R.id.signup_coordinator);

        mDialog = new ProgressDialog(SignUpActivity.this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("Please wait..");
        mDialog.setTitle("Registering user");
    }

    private void addUserToDB(FirebaseUser user, String name, String email){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mRef.child("name").setValue(name);
        mRef.child("email").setValue(email);
    }

    public void signUpUser(View view){
        final String name = nameInput.getText().toString().trim();
        final String email = emailInput.getText().toString().trim();
        final String password = passInput.getText().toString().trim();
        String conf_password = cpassInput.getText().toString().trim();

        if (email.isEmpty()){
            Snackbar.make(frame, "Please enter a valid email", Snackbar.LENGTH_SHORT).show();
        }
        else if (password.isEmpty() || password.length() < 6){
            Snackbar.make(frame, "Please enter atleast 6 character long password", Snackbar.LENGTH_SHORT).show();
        }
        else if (!password.equals(conf_password)){
            Snackbar.make(frame, "Please re-enter the same password", Snackbar.LENGTH_SHORT).show();
        }
        else {
            mDialog.show();
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.dismiss();
                    if (task.isSuccessful()){
                        addUserToDB(mAuth.getCurrentUser(), name, email);
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Snackbar.make(frame, "Error occurred." + task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void goToLogin(View view){
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
}