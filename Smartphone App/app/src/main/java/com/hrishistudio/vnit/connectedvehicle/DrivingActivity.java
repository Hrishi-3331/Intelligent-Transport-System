package com.hrishistudio.vnit.connectedvehicle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.security.Permissions;
import java.util.ArrayList;

public class DrivingActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver mReceiver;
    private IntentFilter filter;
    private TextView notice_board;
    private ArrayList<WifiP2pDevice> vehicles;
    private WifiP2pManager.PeerListListener listener;
    private ListView vehicleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        p2pManager = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = p2pManager.initialize(DrivingActivity.this, getMainLooper(), null);

        notice_board = (TextView)findViewById(R.id.notice_board);
        vehicleList = (ListView)findViewById(R.id.dsrc_list);
        vehicles = new ArrayList<>();
        listener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                vehicles.clear();
                vehicles.addAll(peers.getDeviceList());
                String[] vehicleNames = new String[vehicles.size()];
                for (int i = 0; i < vehicles.size(); i++){
                    vehicleNames[i] = vehicles.get(i).deviceName;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DrivingActivity.this, android.R.layout.simple_list_item_1, vehicleNames);
                vehicleList.setAdapter(adapter);
            }
        };

        mReceiver = new DSRCBroadCastReceiver(p2pManager, channel, listener);
        filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void switchOnWifi(View view) {
        ImageButton button = (ImageButton) view;
        switch (button.getContentDescription().toString()) {
            case "on":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_on));
                button.setContentDescription("off");
                wifiManager.setWifiEnabled(true);
                break;

            case "off":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_off));
                button.setContentDescription("on");
                wifiManager.setWifiEnabled(true);
                break;
        }
    }

    public void startDiscovery(View view) {
        ImageButton button = (ImageButton) view;
        switch (button.getContentDescription().toString()) {
            case "on":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_on));
                button.setContentDescription("off");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DrivingActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                    return;
                }
                p2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        notice_board.setText("Discovering nearby vehicles");
                    }

                    @Override
                    public void onFailure(int reason) {
                        notice_board.setText("Discovering nearby vehicles failed");
                    }
                });
                break;

            case "off":
                button.setImageDrawable(getResources().getDrawable(R.drawable.button_off));
                button.setContentDescription("on");
                break;
        }
    }

    public void goBack(View view){
        finish();
    }
}