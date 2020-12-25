package com.hrishistudio.vnit.connectedvehicle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.UUID;

public class DrivingMode extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 10;
    private static final int BLUETOOTH_REQUEST = 20;
    public static BluetoothSocket socket;
    public static BluetoothAdapter adapter;
    public static ProgressDialog mDialog;
    public static boolean connected = false;
    public static final UUID cUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving_mode);

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

        ImageView image = (ImageView)findViewById(R.id.driving_gif);
        Glide.with(this).load(R.drawable.driving).into(image);
    }

    private void startTrackerService() {
        startService(new Intent(this, TrackingService.class));
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //startTrackerService();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchOnWifi(View view) {
        ImageButton button = (ImageButton) view;
        switch (button.getContentDescription().toString()) {
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

    public void showDialog(View view){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DrivingMode.this);
        LayoutInflater inflater = getLayoutInflater();
        View DialogLayout = inflater.inflate(R.layout.signal_dialog, null);
        builder.setView(DialogLayout);

        TextView titleView = (TextView) DialogLayout.findViewById(R.id.dialog_title);
        TextView messageView = (TextView) DialogLayout.findViewById(R.id.dialog_message);
        ImageView imageView = (ImageView)DialogLayout.findViewById(R.id.dialog_image);

        final android.app.AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        switch (view.getId()){
            case R.id.signal_red:
                break;

            case R.id.signal_green:
                messageView.setText("Go");
                break;

            case R.id.car_message:
                titleView.setText("Vehicle MH 42 V 6888");
                messageView.setText("Turning Right >>");
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.car_card));
                break;
        }

        dialog.show();
    }
}