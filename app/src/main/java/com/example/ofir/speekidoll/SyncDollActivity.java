package com.example.ofir.speekidoll;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class SyncDollActivity extends AppCompatActivity {
    //this will be the bluetooth activity
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private Button sync_doll_button;
    ConnectThread connectThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_doll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sync_doll_button =(Button)findViewById(R.id.sync_doll_button);

        startBluetooth();

    }
    private void startBluetooth(){



        BA =  BluetoothAdapter.getDefaultAdapter();
        //activate device's bluetooth
        if(!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }
        Toast.makeText(getApplicationContext(), "Bluetooth activated",Toast.LENGTH_LONG).show();



        pairedDevices = BA.getBondedDevices();
        String[] device_names = new String[pairedDevices.size()];
        if(pairedDevices.size() > 0){
            //there are pared devices

            int i =0;
            for(BluetoothDevice device : pairedDevices){

                String name = device.getName();
                String MAC = device.getAddress();
                device_names[i++] = name;
            }
        }
        //insert BT to list view
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.listview_content,device_names);
        ListView listView = (ListView) findViewById(R.id.bt_devices);
        listView.setAdapter(adapter);
    }

    //will be called when a bt device is clicked
    public void connect(TextView v){
        String name = v.getText().toString();
        BluetoothDevice bd = null;
        for(BluetoothDevice device : pairedDevices){
            if(name.equals(device.getName())){
                bd  = device;
                break;
            }
        }
        if(bd == null){
            Log.d("BAD", "Bluetooth device wasnt found: " + name);
            return;
        }
        connectThread = new ConnectThread(bd);
        connectThread.run();

    }

    //gets called when the synd_doll_button is clicked
    public void syncDoll() {
        for (int i = StickerData.last_updated; i < StickerData.counter; i++) {
            connectThread.write(StickerData.getData(i));
        }
        StickerData.last_updated  = StickerData.counter;
    }



    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private byte[] mmBuffer; //buffer for stream
        private  InputStream mmInStream;
        private  OutputStream mmOutStream;
        private Handler mHandler; // handler that gets info from Bluetooth service

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(Settings.Secure.ANDROID_ID));
            } catch (IOException e) {
                Log.e("WHYYY", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BA.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("WHYHYHY", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.

            manageMyConnectedSocket(mmSocket);
        }
        private void manageMyConnectedSocket(BluetoothSocket  socket){
            mmBuffer = new byte[1024];
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("Error", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("Error", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            //enable the sync button
            sync_doll_button.setEnabled(true);
        }


        public void write(byte[] bytes) {

            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e("Error", "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }
        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("WHYHYHY", "Could not close the client socket", e);
            }
        }
    }
}
