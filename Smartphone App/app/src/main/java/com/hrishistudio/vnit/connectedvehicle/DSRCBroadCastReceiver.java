package com.hrishistudio.vnit.connectedvehicle;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class DSRCBroadCastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager.PeerListListener listener;

    public DSRCBroadCastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener listener) {
        this.manager = manager;
        this.channel = channel;
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int STATE = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            switch (STATE) {
                case WifiP2pManager.WIFI_P2P_STATE_ENABLED:
                    Toast.makeText(context, "DSRC Wifi On", Toast.LENGTH_SHORT).show();
                    break;

                case WifiP2pManager.WIFI_P2P_STATE_DISABLED:
                    Toast.makeText(context, "DSRC Wifi Off", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            manager.requestPeers(channel, listener);
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }
}
