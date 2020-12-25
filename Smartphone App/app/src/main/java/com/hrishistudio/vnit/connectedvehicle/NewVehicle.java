package com.hrishistudio.vnit.connectedvehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class NewVehicle extends AppCompatActivity {

    private EditText input_reg_no;
    private EditText input_model;
    private EditText input_engine;
    private EditText input_reg_date;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);

        input_reg_no = (EditText)findViewById(R.id.vehicle_reg_number);
        input_model = (EditText)findViewById(R.id.vehicle_model);
        input_engine = (EditText)findViewById(R.id.vehicle_engine);
        input_reg_date = (EditText)findViewById(R.id.vehicle_reg_date);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.new_vehicle_coordinator);

        mDialog = new ProgressDialog(NewVehicle.this);
        mDialog.setTitle("Registering Vehicle");
        mDialog.setMessage("Please wait...");
    }

    public void registerVehicle(View view){
        String reg_no = input_reg_no.getText().toString().trim();
        if (reg_no.isEmpty() || reg_no.length() < 6){
            throwError("Please enter a valid registration number");
            return;
        }

        String model = input_model.getText().toString().trim();
        if (model.isEmpty() || model.length() < 6){
            throwError("Please enter a valid model name");
            return;
        }

        String engine = input_engine.getText().toString().trim();
        if (engine.isEmpty() || engine.length() < 6){
            throwError("Please enter a valid engine number");
            return;
        }

        String reg_date = input_reg_date.getText().toString().trim();
        if (reg_date.isEmpty() || reg_date.length() < 6){
            throwError("Please enter a valid registration date");
            return;
        }

        final String owner = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDialog.show();
        Map<String, String> vehicle = new HashMap<String, String>();
        vehicle.put("registration_number", reg_no);
        vehicle.put("registration_date", reg_date);
        vehicle.put("engine_number", engine);
        vehicle.put("model_name", model);
        vehicle.put("owner", owner);

        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("vehicles").push();
        mRef.setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    registerVehicleWithUser(mRef, owner);
                }
                else{
                    mDialog.dismiss();
                    throwError("Error in registering vehicle. Please try again.");
                }
            }

            private void registerVehicleWithUser(final DatabaseReference mRef, String owner) {
                FirebaseDatabase.getInstance().getReference().child("users").child(owner).child("vehicles").push().setValue(mRef.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mDialog.dismiss();
                            finish();
                        }
                        else {
                            mDialog.dismiss();
                            throwError("Error in registering vehicle. Please try again.");
                            mRef.removeValue();
                        }
                    }
                });
            }
        });
    }

    private void throwError(String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public void goBack(View view){
        finish();
    }
}