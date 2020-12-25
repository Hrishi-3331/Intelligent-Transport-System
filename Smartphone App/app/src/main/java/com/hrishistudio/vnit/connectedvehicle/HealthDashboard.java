package com.hrishistudio.vnit.connectedvehicle;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class HealthDashboard extends AppCompatActivity {
    private Interpreter interpreter;
    private TextView final_status;
    private TextView obd_status;
    private TextView baro_pressure;
    private TextView rpm;
    private TextView maf;
    private TextView manifold_press;
    private TextView coolant_temp;
    private TextView speed;
    private TextView throttle_pos;

    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    private static ProgressDialog mDialog;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_dashboard);
        final_status = (TextView)findViewById(R.id.obd_final_status);
        obd_status = (TextView)findViewById(R.id.obd_status);
        baro_pressure = (TextView)findViewById(R.id.obd_bp);
        rpm = (TextView)findViewById(R.id.obd_rpm);
        maf = (TextView)findViewById(R.id.obd_maf);
        manifold_press = (TextView)findViewById(R.id.obd_manifold);
        coolant_temp = (TextView)findViewById(R.id.obd_coolant_temp);
        speed = (TextView)findViewById(R.id.obd_speed);
        throttle_pos = (TextView)findViewById(R.id.obd_throttle);

        try {
            interpreter = new Interpreter(loadModelFile(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSocket = null;
        mDialog = new ProgressDialog(HealthDashboard.this);
        mDialog.setTitle("Connecting with car");
        mDialog.setMessage("Please wait");
        mDialog.setCanceledOnTouchOutside(false);
//        mDialog.show();
        deviceAddress = "56:79:1F:09:51:15";

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
//        createConnectThread.start();

        /*
        Second most important piece of Code. GUI Handler
         */
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        mDialog.dismiss();
                        switch(msg.arg1){
                            case 1:
                                obd_status.setText("Connected");
                                Toast.makeText(HealthDashboard.this, "Connection success", Toast.LENGTH_SHORT).show();
                                break;
                            case -1:
                                obd_status.setText("Failed");
                                Toast.makeText(HealthDashboard.this, "Connection failed", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        //Toast.makeText(HealthDashboard.this, "" + arduinoMsg, Toast.LENGTH_SHORT).show();
                        String[] data = arduinoMsg.split(" ");
                        double[] input = new double[7];
                        for (int i = 0; i < 7; i++){
                            input[i] = Double.parseDouble(data[i].trim());
                        }
                        run(input);
                        break;
                }
            }
        };

    }

    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();


            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                System.out.println("Error in creating socket!");
            }
            mSocket = tmp;
        }

        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                mSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                try {
                    mSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e("TAG", "Could not close the client socket", closeException);
                }
                return;
            }

            connectedThread = new ConnectedThread(mSocket);
            connectedThread.run();
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e("TAG", "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */

    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
    }

    /** ML Code Functions below. Nothing to do with bluetooth */

    /*

    private float getInferences(double[] in){
        float[] input = new float[in.length];
        for(int i = 0; i < in.length; i++){
            input[i] = (float)in[i];
        }
        float[][] output = new float[1][1];
        interpreter.run(input, output);
        return output[0][0];
    } */


    private float getInferences(double[] in){
        float[] input = new float[4];
        input[0] = (float)in[3];
        input[1] = (float)in[1];
        input[2] = (float)in[4];
        input[3] = (float)in[6];
        float[][] output = new float[1][16];
        interpreter.run(input, output);
        int res = 0;
        for(int i = 0; i < 16; i++){
            if(output[0][i] > output[0][res]){
                res = i;
            }
        }
        return res;
    }

    private void run(double[] parameters){
        setVisualData(parameters);
        int res = Math.round(getInferences(parameters));
        TextView[] views = new TextView[]{manifold_press, coolant_temp, maf, throttle_pos};
        if (res == 0) {
            final_status.setText("All OK");
            for (TextView view:views){
                view.setTextColor(Color.WHITE);
            }
        }
        else {
            final_status.setText("Anomaly Found");
            String bin = Integer.toBinaryString(res);

            if (bin.length() < 4){
                for (int i = 0; i < 4 - bin.length(); i++){
                    bin = "0" + bin;
                }
            }

            int sens1 = Integer.parseInt(String.valueOf(bin.charAt(0)));
            int sens2 = Integer.parseInt(String.valueOf(bin.charAt(1)));
            int sens3 = Integer.parseInt(String.valueOf(bin.charAt(2)));
            int sens4 = Integer.parseInt(String.valueOf(bin.charAt(3)));

            int[] sensors = new int[]{sens1, sens2, sens3, sens4};

            for (int i = 0; i < sensors.length; i++){
                if (sensors[i] != 0){
                    views[i].setTextColor(Color.RED);
                }
                else{
                    views[i].setTextColor(Color.WHITE);
                }
            }
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor descriptor = HealthDashboard.this.getAssets().openFd("model2.tflite");
        FileInputStream inputStream = new FileInputStream(descriptor.getFileDescriptor());
        FileChannel channel = inputStream.getChannel();
        long startOffset = descriptor.getStartOffset();
        long length = descriptor.getLength();
        return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, length);
    }

    private void setVisualData(double[] parameters){
        TextView[] views = new TextView[]{baro_pressure, coolant_temp, rpm, manifold_press, maf, speed, throttle_pos};
        for (int i = 0; i < 7; i++){
            views[i].setText(String.format("%.2f", parameters[i]));
        }
    }

    private double[] testManually(int par){
        double[] test_a_1 = new double[]{100, 84, 1582, 82, 26.92, 44, 0.369};
        double[] test_a_2 = new double[]{99, 85, 1657, 37, 9.29, 36, 0.278};
        double[] test_a_3 = new double[]{100, 84, 2378, 51, 21.59, 51, 0.337};
        double[] test_a_4 = new double[]{100, 83, 1952, 19, 5.26, 57, 0.231};
        double[] test_a_5 = new double[]{100, 85, 1970, 30, 5.79, 40, 0.263};

        double[] test_b_1 = new double[]{95.31741351, 104.7025396, 4793.486993, 69.29570808, 51.24776308, 56.74860845, 0.700598859};
        double[] test_b_2 = new double[]{96.34046066, 104.4444719, 3649.884731, 58.3582343, 68.12643359, 11.38372754, 0.637215804};
        double[] test_b_3 = new double[]{96.00609981, 106.0884159, 4274.113988, 20.54871859, 63.9619993, 18.15175421, 0.829642283};
        double[] test_b_4 = new double[]{95.57697447, 102.9505734, 5273.422264, 20.86436117, 69.91090136, 67.0854193, 0.717417257};
        double[] test_b_5 = new double[]{96.49999507, 104.4678611, 4448.257524, 64.59677932, 59.59896043, 31.56277981, 0.610496836};

        int random = new Random().nextInt(4);
        double[][] good_data = new double[][]{test_a_1, test_a_2, test_a_3, test_a_4, test_a_5};
        double[][] bad_data = new double[][]{test_b_1, test_b_2, test_b_3, test_b_4, test_b_5};
        if (par == 0) return good_data[random];
        else return bad_data[random];
    }

    public void goBack(View view){
        finish();
    }

    public void testGood(View view){
        run(testManually(0));
    }

    public void testBad(View view){
        run(testManually(1));
    }
}