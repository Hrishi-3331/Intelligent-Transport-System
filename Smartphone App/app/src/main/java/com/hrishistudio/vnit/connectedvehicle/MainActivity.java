package com.hrishistudio.vnit.connectedvehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView mNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawer = (DrawerLayout)findViewById(R.id.mDrawer);
        mNavigation = (NavigationView) findViewById(R.id.mNavigation);

        fetchName();

        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.drawer_profile:
                        startActivity(new Intent(MainActivity.this, DrivingMode.class));
                        break;

                    case R.id.drawer_dashboard:
                        startActivity(new Intent(MainActivity.this, HealthDashboard.class));
                        break;

                    case R.id.drawer_navigation:
                        //tartActivity(new Intent(MainActivity.this, HospitalReviews.class));
                        break;

                    case R.id.drawer_about:
                        //startActivity(new Intent(MainActivity.this, AboutUs.class));
                        break;

                    case R.id.drawer_fine:
                        //startActivity(new Intent(MainActivity.this, FineLogs.class));
                        break;

                    case R.id.vehicles:
                        startActivity(new Intent(MainActivity.this, VehicleList.class));
                        break;

                    default:
                        break;
                }
                mDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void fetchName(){
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Connecting");
        dialog.setMessage("Please wait");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    ((TextView)findViewById(R.id.user_name_home)).setText(snapshot.getValue().toString());
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void toggleMenu(View view){
        mDrawer.openDrawer(GravityCompat.START);
    }

    public void toggleSwitch(View view){
        ImageButton button = (ImageButton)view;
        switch (button.getContentDescription().toString()){
            case "on":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_on));
                button.setContentDescription("off");
                break;

            case "off":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_off));
                button.setContentDescription("on");
                break;
        }
    }
}